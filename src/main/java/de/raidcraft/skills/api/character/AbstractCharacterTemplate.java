package de.raidcraft.skills.api.character;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.combat.action.Attack;
import de.raidcraft.skills.api.effect.Effect;
import de.raidcraft.skills.api.effect.Stackable;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.party.Party;
import de.raidcraft.skills.api.party.SimpleParty;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.trigger.TriggerManager;
import de.raidcraft.skills.api.trigger.Triggered;
import de.raidcraft.skills.trigger.HealTrigger;
import de.raidcraft.util.BukkitUtil;
import de.raidcraft.util.LocationUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * @author Silthus
 */
public abstract class AbstractCharacterTemplate implements CharacterTemplate {

    private static final Random RANDOM = new Random();

    private final String name;
    private final LivingEntity entity;
    private final Map<Class<? extends Effect>, Effect> effects = new HashMap<>();
    // every player is member of his own party by default
    private Party party;
    private int damage;
    private boolean inCombat = false;
    private long lastSwing;

    public AbstractCharacterTemplate(LivingEntity entity) {

        this.entity = entity;
        this.name = (entity instanceof Player) ? ((Player) entity).getName() : entity.getType().getName();
    }

    protected AbstractCharacterTemplate(String name) {

        this.entity = Bukkit.getPlayer(name);
        this.name = name;
        this.party = new SimpleParty(this);
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public LivingEntity getEntity() {

        return entity;
    }

    @Override
    public int getDamage() {

        return damage;
    }

    @Override
    public void setDamage(int damage) {

        this.damage = damage;
    }

    @Override
    public int getHealth() {

        return getEntity().getHealth();
    }

    @Override
    public void setHealth(int health) {

        getEntity().setHealth(health);
    }

    @Override
    public int getMaxHealth() {

        return getEntity().getMaxHealth();
    }

    @Override
    public void setMaxHealth(int maxHealth) {

        getEntity().setMaxHealth(maxHealth);
    }

    private void damage(int damage) {

        int newHealth = getHealth() - damage;
        if (newHealth <= 0) {
            kill();
        } else {
            setHealth(newHealth);
        }
        getEntity().playEffect(EntityEffect.HURT);
        getEntity().getWorld().playSound(
                getEntity().getLocation(), getDeathSound(getEntity().getType()), getSoundStrength(getEntity()), 1.0F);
        if (this instanceof Hero) {
            ((Hero) this).debug("You took: " + damage + "dmg - [" + newHealth + "]");
        }
    }

    @Override
    public void damage(Attack attack) {

        if (!attack.isCancelled() && attack.getDamage() > 0) {
            damage(attack.getDamage());
            // lets set some bukkit properties
            getEntity().setLastDamage(attack.getDamage());
            if (attack.getSource() instanceof Hero) {
                ((Hero) attack.getSource()).debug(
                        "You->" + getName() + ": " + attack.getDamage() + "dmg - " + getName() + "[" + getHealth() + "]");
                ((Hero) attack.getSource()).combatLog("Du hast " + attack.getTarget() + " " + attack.getDamage() + " Schaden zugefügt.");
            }
            if (attack.getTarget() instanceof Hero) {
                ((Hero) attack.getTarget()).combatLog("Du hast " + attack.getDamage() + " Schaden von " + attack.getSource() + " erhalten.");
            }
        }
    }

    @Override
    public void heal(int amount) {

        try {
            HealTrigger trigger = TriggerManager.callTrigger(
                    new HealTrigger(this, amount)
            );

            if (trigger.isCancelled()) {
                return;
            }

            amount = trigger.getAmount();

            int newHealth = getHealth() + amount;
            if (newHealth > getMaxHealth()) newHealth = getMaxHealth();
            getEntity().setNoDamageTicks(1);
            setHealth(newHealth);
            if (this instanceof Hero) {
                ((Hero)this).combatLog("Du wurdest um " + amount + "HP geheilt.");
            }
        } catch (CombatException e) {
            if (this instanceof Hero) {
                ((Hero)this).sendMessage(ChatColor.RED + e.getMessage());
            }
        }
    }

    @Override
    public void kill(CharacterTemplate killer) {

        kill();
        if (killer instanceof Hero) {
            ((Hero) killer).debug("YOU killed " + getName());
        }
    }

    @Override
    public void kill() {

        setHealth(0);
    }

    public <E extends Effect> void addEffect(Class<E> eClass, E effect) throws CombatException {

        if (hasEffect(eClass)) {
            Effect<?> existingEffect = effects.get(eClass);
            // lets check priorities
            if (existingEffect instanceof Stackable) {
                // we dont replace or renew stackable effects, we increase their stacks :)
                ((Stackable) existingEffect).setStacks(((Stackable) existingEffect).getStacks() + 1);
            } else if (existingEffect.getPriority() < 0) {
                // prio less then 0 is special and means always replace
                existingEffect.remove();
                addEffect(eClass, effect);
            } else if (existingEffect.getPriority() > effect.getPriority()) {
                throw new CombatException("Es ist bereits ein stärkerer Effekt aktiv!");
            } else if (existingEffect.getPriority() == effect.getPriority()) {
                // lets renew the existing effect
                existingEffect.renew();
            } else {
                // the new effect has a higher priority so lets remove the old one
                existingEffect.remove();
                addEffect(eClass, effect);
            }
        } else {
            // apply the new effect
            effects.put(eClass, effect);
            effect.apply();
        }
    }

    @Override
    public final <E extends Effect<S>, S> E addEffect(Skill skill, S source, Class<E> eClass) throws CombatException {

        E effect = RaidCraft.getComponent(SkillsPlugin.class).getEffectManager().getEffect(source, this, eClass, skill);
        addEffect(eClass, effect);
        return effect;
    }

    @Override
    public final <E extends Effect<S>, S> E addEffect(S source, Class<E> eClass) throws CombatException {

        E effect = RaidCraft.getComponent(SkillsPlugin.class).getEffectManager().getEffect(source, this, eClass);
        addEffect(eClass, effect);
        return effect;
    }

    @Override
    public void removeEffect(Effect effect) throws CombatException {

        // lets silently remove the effect from the list of applied effects
        // we asume the remove() method of the effect has already been called at this point
        effects.remove(effect.getClass());
        // lets remove the effect as a listener
        if (effect instanceof Triggered) {
            TriggerManager.unregisterListeners((Triggered) effect);
        }
    }

    @Override
    public <E> void removeEffect(Class<E> eClass) throws CombatException {

        Effect<?> effect = effects.remove(eClass);
        if (effect != null) {
            effect.remove();
        }
    }

    @Override
    public final void clearEffects() {

        for (Effect effect : new ArrayList<>(effects.values())) {
            try {
                if (effect != null) {
                    effect.remove();
                }
            } catch (CombatException e) {
                if (effect.getTarget() instanceof Hero) {
                    ((Hero) effect.getTarget()).sendMessage(ChatColor.RED + e.getMessage());
                }
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E extends Effect> E getEffect(Class<E> eClass) {

        return (E) effects.get(eClass);
    }

    @Override
    public <E extends Effect> boolean hasEffect(Class<E> eClass) {

        return effects.containsKey(eClass);
    }

    @Override
    public final boolean hasEffectType(EffectType type) {

        for (Effect effect : effects.values()) {
            if (effect.isOfType(type)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public final void removeEffectTypes(EffectType type) throws CombatException {

        for (Effect effect : new ArrayList<>(effects.values())) {
            if (effect.isOfType(type)) {
                effect.remove();
            }
        }
    }

    public final List<Effect> getEffects() {

        return new ArrayList<>(effects.values());
    }

    @Override
    public Set<CharacterTemplate> getTargetsInFront(int range, float degrees) throws CombatException {

        Set<CharacterTemplate> targets = new HashSet<>();
        List<LivingEntity> nearbyEntities = BukkitUtil.getLivingEntitiesInCone(getEntity(), range, degrees);

        if (nearbyEntities.size() < 1) throw new CombatException("Keine Zeile in Reichweite von " + range + "m.");

        for (LivingEntity target : nearbyEntities) {
            targets.add(RaidCraft.getComponent(SkillsPlugin.class).getCharacterManager().getCharacter(target));
        }

        return targets;
    }

    @Override
    public Set<CharacterTemplate> getTargetsInFront(int range) throws CombatException {

        return getTargetsInFront(range, 45.0F);
    }

    @Override
    public Set<CharacterTemplate> getTargetsInFront() throws CombatException {

        return getTargetsInFront(15, 45.0F);
    }

    @Override
    public Set<CharacterTemplate> getNearbyTargets() throws CombatException {

        return getNearbyTargets(30);
    }

    @Override
    public Set<CharacterTemplate> getNearbyTargets(int range) throws CombatException {

        Set<CharacterTemplate> targets = new HashSet<>();
        List<LivingEntity> nearbyEntities = BukkitUtil.getNearbyEntities(getEntity(), range);
        if (nearbyEntities.size() < 1) throw new CombatException("Keine Zeile in Reichweite von " + range + "m.");
        for (LivingEntity target : nearbyEntities) {
            targets.add(RaidCraft.getComponent(SkillsPlugin.class).getCharacterManager().getCharacter(target));
        }
        return targets;
    }

    @Override
    public CharacterTemplate getTarget() throws CombatException {

        return getTarget(100);
    }

    @Override
    public CharacterTemplate getTarget(int range) throws CombatException {

        LivingEntity target = BukkitUtil.getTargetEntity(getEntity(), LivingEntity.class);
        if (target == null) {
            throw new CombatException("Du hast kein Ziel anvisiert!");
        }
        if (LocationUtil.getBlockDistance(target.getLocation(), getEntity().getLocation()) > range) {
            throw new CombatException("Ziel ist nicht in Reichweite. Max. Reichweite: " + range + "m");
        }
        return RaidCraft.getComponent(SkillsPlugin.class).getCharacterManager().getCharacter(target);
    }

    @Override
    public Location getBlockTarget() throws CombatException {

        return getBlockTarget(100);
    }

    @Override
    public Location getBlockTarget(int range) throws CombatException {

        Block block = getEntity().getTargetBlock(null, range);
        if (block == null
                || LocationUtil.getBlockDistance(block.getLocation(), getEntity().getLocation()) > range) {
            throw new CombatException("Ziel ist nicht in Reichweite. Max. Reichweite: " + range + "m");
        }
        return block.getLocation();
    }

    @Override
    public boolean isBehind(CharacterTemplate target) {

        // we asume that if the target cannot see us we are behind it
        return target.getEntity().hasLineOfSight(getEntity());
    }

    @Override
    public boolean isInCombat() {

        return inCombat;
    }

    @Override
    public void setInCombat(boolean inCombat) {

        this.inCombat = inCombat;
    }

    @Override
    public boolean canSwing() {

        return System.currentTimeMillis() > getLastSwing();
    }

    @Override
    public long getLastSwing() {

        return lastSwing;
    }

    @Override
    public void setLastSwing() {

        this.lastSwing = System.currentTimeMillis()
                + (long) (RaidCraft.getComponent(SkillsPlugin.class).getCommonConfig().swing_delay * 1000);
    }

    @Override
    public String toString() {

        return getName();
    }

    protected float getSoundStrength(LivingEntity target) {

        if (!(target instanceof Ageable)) {
            return 1.0F;
        }
        if (((Ageable) target).isAdult()) {
            return (RANDOM.nextFloat() - RANDOM.nextFloat()) * 0.2F + 1.0F;
        } else {
            return (RANDOM.nextFloat() - RANDOM.nextFloat()) * 0.2F + 1.5F;
        }
    }

    protected Sound getDeathSound(EntityType type) {

        switch (type) {

            case COW:
                return Sound.COW_IDLE;
            case BLAZE:
                return Sound.BLAZE_DEATH;
            case CHICKEN:
                return Sound.CHICKEN_HURT;
            case CREEPER:
                return Sound.CREEPER_DEATH;
            case SKELETON:
                return Sound.SKELETON_DEATH;
            case IRON_GOLEM:
                return Sound.IRONGOLEM_DEATH;
            case GHAST:
                return Sound.GHAST_DEATH;
            case PIG:
                return Sound.PIG_DEATH;
            case OCELOT:
                return Sound.CAT_HIT;
            case SHEEP:
                return Sound.SHEEP_IDLE;
            case SPIDER:
            case CAVE_SPIDER:
                return Sound.SPIDER_DEATH;
            case WOLF:
                return Sound.WOLF_DEATH;
            case ZOMBIE:
                return Sound.ZOMBIE_DEATH;
            case PIG_ZOMBIE:
                return Sound.ZOMBIE_PIG_DEATH;
            default:
                return Sound.HURT_FLESH;
        }
    }

    @Override
    public Party getParty() {

        return party;
    }

    @Override
    public boolean isInParty(Party party) {

        return party.isInGroup(this);
    }

    @Override
    public void joinParty(Party party) {

        if (!this.party.equals(party)) {
            this.party = party;
            party.addMember(this);
        }
    }

    @Override
    public void leaveParty(Party party) {

        if (this.party.equals(party)) {
            this.party = new SimpleParty(this);
            party.removeMember(this);
        }
    }

    @Override
    public boolean isFriendly(CharacterTemplate source) {

        return getParty().isInGroup(source);
    }
}
