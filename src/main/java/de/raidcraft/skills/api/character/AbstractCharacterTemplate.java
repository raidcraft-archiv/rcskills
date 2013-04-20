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
import de.raidcraft.skills.items.ArmorPiece;
import de.raidcraft.skills.items.ArmorType;
import de.raidcraft.skills.items.Weapon;
import de.raidcraft.skills.trigger.HealTrigger;
import de.raidcraft.util.BukkitUtil;
import de.raidcraft.util.EffectUtil;
import de.raidcraft.util.LocationUtil;
import de.raidcraft.util.MathUtil;
import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Silthus
 */
public abstract class AbstractCharacterTemplate implements CharacterTemplate {

    private final String name;
    private final LivingEntity entity;
    private final Map<Class<? extends Effect>, Effect> effects = new HashMap<>();
    private final Map<Weapon.Slot, Weapon> weapons = new EnumMap<>(Weapon.Slot.class);
    private final Map<Weapon.Slot, Long> lastSwing = new EnumMap<>(Weapon.Slot.class);
    private final Map<ArmorType, ArmorPiece> armorPieces = new EnumMap<>(ArmorType.class);
    // every player is member of his own party by default
    private Party party;
    private int damage;
    private boolean inCombat = false;
    private Attack lastAttack;

    public AbstractCharacterTemplate(LivingEntity entity) {

        this.entity = entity;
        if (entity != null) {
            if (entity.getCustomName() != null && !entity.getCustomName().equals("")) {
                this.name = entity.getCustomName();
            } else if (entity instanceof Player) {
                this.name = ((Player) entity).getName();
            } else {
                this.name = entity.getType().getName();
            }
        } else {
            this.name = "UNKNOWN";
        }
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
    public Weapon getWeapon(Weapon.Slot slot) {

        return weapons.get(slot);
    }

    @Override
    public boolean hasWeapon(Weapon.Slot slot) {

        return weapons.containsKey(slot);
    }

    @Override
    public void setWeapon(Weapon weapon) {

        weapons.put(weapon.getSlot(), weapon);
    }

    @Override
    public void removeWeapon(Weapon.Slot slot) {

        weapons.remove(slot);
    }

    @Override
    public boolean canSwing(Weapon.Slot slot) {

        if (this instanceof Hero) {
            int itemSlot = ((Hero) this).getPlayer().getInventory().getHeldItemSlot();
            if (itemSlot != getWeapon(slot).getTaskBarSlot()) {
                return false;
            }
        }
        return System.currentTimeMillis() > getLastSwing(slot);
    }

    @Override
    public long getLastSwing(Weapon.Slot slot) {

        if (!lastSwing.containsKey(slot)) {
            return 0;
        }
        return lastSwing.get(slot);
    }

    @Override
    public void setLastSwing(Weapon.Slot slot) {

        long lastSwing = System.currentTimeMillis() + (long)(getWeapon(slot).getSwingTime() * 1000);
        this.lastSwing.put(slot, lastSwing);
    }

    @Override
    public Attack getLastDamageCause() {

        return lastAttack;
    }

    @Override
    public Collection<ArmorPiece> getArmor() {

        return armorPieces.values();
    }

    @Override
    public ArmorPiece getArmor(ArmorType slot) {

        return armorPieces.get(slot);
    }

    @Override
    public void setArmor(ArmorPiece armorPiece) {

        armorPieces.put(armorPiece.getType(), armorPiece);
    }

    @Override
    public void removeArmor(ArmorType type) {

        armorPieces.remove(type);
    }

    @Override
    public void clearArmor() {

        armorPieces.clear();
    }

    @Override
    public boolean hasArmor(ArmorType slot) {

        return armorPieces.containsKey(slot);
    }

    @Override
    public int getDamage() {

        int damage = this.damage;
        for (Weapon.Slot slot : Weapon.Slot.values()) {
            Weapon weapon = getWeapon(slot);
            if (weapon != null) {
                damage += weapon.getDamage();
            }
        }
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

        if (getEntity() == null) return;
        if (health > getMaxHealth()) {
            health = getMaxHealth();
        }
        if (getEntity().getHealth() < 1 && health < 1) {
            return;
        }
        getEntity().setHealth(health);
    }

    @Override
    public int getMaxHealth() {

        return getEntity().getMaxHealth();
    }

    @Override
    public void setMaxHealth(int maxHealth) {

        if (getEntity() == null) return;
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
            // set the last attack variable to track death
            lastAttack = attack;
            if (attack.getSource() instanceof Hero) {
                ((Hero) attack.getSource()).debug(
                        "You->" + getName() + ": " + attack.getDamage() + "dmg - " + getName() + "[" + getHealth() + "]");
                ((Hero) attack.getSource()).combatLog("Du hast " + getName() + " " + attack.getDamage() + " Schaden zugefügt.");
            }
            if (this instanceof Hero) {
                ((Hero)this).combatLog("Du hast " + attack.getDamage() + " Schaden von " + attack.getSource() + " erhalten.");
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
            // lets fake some wolf hearts for visuals
            EffectUtil.fakeWolfHearts(getEntity().getLocation());
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
        // play the death sound
        getEntity().getWorld().playSound(
                getEntity().getLocation(),
                getDeathSound(getEntity().getType()),
                1.0F,
                getSoundStrength(getEntity())
        );
        // play the death effect
        getEntity().playEffect(EntityEffect.DEATH);
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

        if (source instanceof Skill) {
            return addEffect((Skill) source, source, eClass);
        }
        E effect = RaidCraft.getComponent(SkillsPlugin.class).getEffectManager().getEffect(source, this, eClass);
        addEffect(eClass, effect);
        return effect;
    }

    @Override
    public void removeEffect(Effect effect) throws CombatException {

        Effect removedEffect = effects.remove(effect.getClass());
        if (removedEffect != null) {
            effect.remove();
        }
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

    @Override
    public final List<Effect> getEffects() {

        return new ArrayList<>(effects.values());
    }

    @Override
    public final List<Effect> getEffects(EffectType... types) {

        List<Effect> effects = new ArrayList<>();
        OUTTER: for (Effect effect : this.effects.values()) {
            for (EffectType type : types) {
                if (!effect.isOfType(type)) {
                    continue OUTTER;
                }
            }
            effects.add(effect);
        }
        return effects;
    }

    @Override
    public List<CharacterTemplate> getTargetsInFront(int range, float degrees) throws CombatException {

        List<CharacterTemplate> targets = new ArrayList<>();
        List<LivingEntity> nearbyEntities = BukkitUtil.getLivingEntitiesInCone(getEntity(), range, degrees);

        if (nearbyEntities.size() < 1) throw new CombatException("Keine Zeile in Reichweite von " + range + "m.");

        for (LivingEntity target : nearbyEntities) {
            targets.add(RaidCraft.getComponent(SkillsPlugin.class).getCharacterManager().getCharacter(target));
        }

        return targets;
    }

    @Override
    public List<CharacterTemplate> getTargetsInFront(int range) throws CombatException {

        return getTargetsInFront(range, 45.0F);
    }

    @Override
    public List<CharacterTemplate> getTargetsInFront() throws CombatException {

        return getTargetsInFront(15, 45.0F);
    }

    @Override
    public List<CharacterTemplate> getNearbyTargets() throws CombatException {

        return getNearbyTargets(30);
    }

    @Override
    public List<CharacterTemplate> getNearbyTargets(int range) throws CombatException {

        List<CharacterTemplate> targets = new ArrayList<>();
        List<LivingEntity> nearbyEntities = BukkitUtil.getNearbyEntities(getEntity(), range);
        if (nearbyEntities.size() < 1) throw new CombatException("Keine Zeile in Reichweite von " + range + "m.");
        for (LivingEntity target : nearbyEntities) {
            targets.add(RaidCraft.getComponent(SkillsPlugin.class).getCharacterManager().getCharacter(target));
        }
        return targets;
    }

    public List<CharacterTemplate> getNearbyTargets(int range, boolean friendly) throws CombatException {

        List<CharacterTemplate> nearbyTargets = getNearbyTargets(range);
        if (!friendly) {
            List<CharacterTemplate> targets = new ArrayList<>();
            for (CharacterTemplate target : nearbyTargets) {
                if (!target.isFriendly(this)) {
                    targets.add(target);
                }
            }
            if (targets.size() < 1) {
                throw new CombatException(CombatException.Type.INVALID_TARGET);
            }
            return targets;
        }
        return nearbyTargets;
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
    public String toString() {

        return getName();
    }

    protected float getSoundStrength(LivingEntity target) {

        if (!(target instanceof Ageable)) {
            return 1.0F;
        }
        if (((Ageable) target).isAdult()) {
            return (MathUtil.RANDOM.nextFloat() - MathUtil.RANDOM.nextFloat()) * 0.2F + 1.0F;
        } else {
            return (MathUtil.RANDOM.nextFloat() - MathUtil.RANDOM.nextFloat()) * 0.2F + 1.5F;
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
    public void leaveParty() {

        if (this.party != null) {
            party.removeMember(this);
        }
        this.party = new SimpleParty(this);
    }

    @Override
    public boolean isFriendly(CharacterTemplate source) {

        return getParty().isInGroup(source);
    }
}
