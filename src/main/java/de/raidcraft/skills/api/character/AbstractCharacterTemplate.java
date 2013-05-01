package de.raidcraft.skills.api.character;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.combat.ThreatTable;
import de.raidcraft.skills.api.combat.action.Attack;
import de.raidcraft.skills.api.combat.action.EnvironmentAttack;
import de.raidcraft.skills.api.combat.action.HealAction;
import de.raidcraft.skills.api.effect.Effect;
import de.raidcraft.skills.api.effect.Stackable;
import de.raidcraft.skills.api.effect.common.Combat;
import de.raidcraft.skills.api.events.RCEntityDeathEvent;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.AttachedLevel;
import de.raidcraft.skills.api.party.Party;
import de.raidcraft.skills.api.party.SimpleParty;
import de.raidcraft.skills.api.ability.Ability;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.trigger.TriggerManager;
import de.raidcraft.skills.api.trigger.Triggered;
import de.raidcraft.skills.items.ArmorPiece;
import de.raidcraft.skills.items.ArmorType;
import de.raidcraft.skills.items.Weapon;
import de.raidcraft.util.BukkitUtil;
import de.raidcraft.util.EffectUtil;
import de.raidcraft.util.LocationUtil;
import de.raidcraft.util.MathUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

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
    private final ThreatTable threatTable;
    private final Map<Class<? extends Effect>, Effect> effects = new HashMap<>();
    private final Map<Weapon.Slot, Weapon> weapons = new EnumMap<>(Weapon.Slot.class);
    private final Map<Weapon.Slot, Long> lastSwing = new EnumMap<>(Weapon.Slot.class);
    private final Map<ArmorType, ArmorPiece> armorPieces = new EnumMap<>(ArmorType.class);
    protected int maxLevel;
    // every player is member of his own party by default
    private Party party;
    private int damage;
    private boolean inCombat = false;
    private Attack lastAttack;
    private BukkitTask deathTask;
    private AttachedLevel<CharacterTemplate> attachedLevel;

    public AbstractCharacterTemplate(LivingEntity entity) {

        this.entity = entity;
        this.threatTable = new ThreatTable(this);
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
    public ThreatTable getThreatTable() {

        return threatTable;
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
    public void clearWeapons() {

        weapons.clear();
    }

    @Override
    public boolean canSwing(Weapon.Slot slot) {

        if (this instanceof Hero) {
            int itemSlot = ((Hero) this).getPlayer().getInventory().getHeldItemSlot();
            Weapon weapon = getWeapon(slot);
            if (weapon != null && itemSlot != weapon.getTaskBarSlot()) {
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

        Weapon weapon = getWeapon(slot);
        long swingTime = 1000;
        if (weapon != null) {
            swingTime *= weapon.getSwingTime();
        } else {
            swingTime *= RaidCraft.getComponent(SkillsPlugin.class).getCommonConfig().default_swing_time;
        }
        long lastSwing = System.currentTimeMillis() + swingTime;
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

        if (getEntity() == null) {
            RaidCraft.LOGGER.warning("Entity " + getName() + " is NULL on getHealth() call!");
            return 0;
        }
        return getEntity().getHealth();
    }

    @Override
    public void setHealth(int health) {

        if (getEntity() == null) return;
        if (health > getMaxHealth()) {
            health = getMaxHealth();
        }
        if (health < 0) {
            health = 0;
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
            getEntity().playEffect(EntityEffect.HURT);
            getEntity().getWorld().playSound(
                    getEntity().getLocation(), getDeathSound(getEntity().getType()), getSoundStrength(getEntity()), 1.0F);
        }
        if (this instanceof Hero) {
            ((Hero) this).debug("You took: " + damage + "dmg - [" + newHealth + "]");
        }
    }

    @Override
    public void damage(Attack attack) {

        if (!attack.isCancelled() && attack.getDamage() > 0) {
            // this all needs to happen before we damage the entity because of the events that are fired
            if (!(attack instanceof EnvironmentAttack)) {
                // set the last attack variable to track death
                lastAttack = attack;
                // lets increase the thread against the attacker
                if (attack.getSource() instanceof CharacterTemplate) {
                    getThreatTable().getThreatLevel((CharacterTemplate) attack.getSource()).increaseThreat(attack.getThreat());
                }
            }
            // lets set some bukkit properties
            getEntity().setLastDamage(attack.getDamage());
            damage(attack.getDamage());
            // lets do some USK18+ gore effects
            // BLOOOOOOOOOOOOOOOOOOOD!!!!!!
            // 152 = redstone block
            EffectUtil.playEffect(getEntity().getLocation().add(0, 1, 0), org.bukkit.Effect.STEP_SOUND, 152, attack.getDamage() > 100 ? 5 : 1);
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
    public void heal(HealAction action) {

        int newHealth = getHealth() + action.getAmount();
        if (newHealth > getMaxHealth()) newHealth = getMaxHealth();
        // lets increase the threat
        if (action.getSource() instanceof CharacterTemplate) {
            getThreatTable().getThreatLevel((CharacterTemplate) action.getSource()).increaseThreat(action.getThreat());
        }
        getEntity().setNoDamageTicks(1);
        setHealth(newHealth);
        // lets fake some wolf hearts for visuals
        EffectUtil.fakeWolfHearts(getEntity().getLocation());
        if (this instanceof Hero) {
            ((Hero)this).combatLog("Du wurdest von " + action.getSource() + " um " + action.getAmount() + "HP geheilt.");
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

        RaidCraft.callEvent(new RCEntityDeathEvent(this));
        setHealth(0);
        clearEffects();
        // play the death sound
        getEntity().getWorld().playSound(
                getEntity().getLocation(),
                getDeathSound(getEntity().getType()),
                1.0F,
                getSoundStrength(getEntity())
        );
        // play the death effect
        getEntity().playEffect(EntityEffect.DEATH);
        if (!(this instanceof Hero)) {
            if (deathTask == null) {
                deathTask = Bukkit.getScheduler().runTaskLater(RaidCraft.getComponent(SkillsPlugin.class), new Runnable() {
                    @Override
                    public void run() {

                        getEntity().remove();
                    }
                }, 60L);
            }
        }
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
    public <E extends Effect<S>, S> E addEffect(Ability ability, S source, Class<E> eClass) throws CombatException {

        E effect = RaidCraft.getComponent(SkillsPlugin.class).getEffectManager().getEffect(source, this, eClass, ability);
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
        // special check for the combat effect
        if (effect instanceof Combat && source instanceof CharacterTemplate) {
            ((Combat) effect).addInvolvedCharacter((CharacterTemplate) source);
        }
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
        if (!inCombat) {
            getThreatTable().reset();
        }
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

    @Override
    public Material getItemTypeInHand() {

        ItemStack itemInHand = getEntity().getEquipment().getItemInHand();
        if (itemInHand == null || itemInHand.getTypeId() == 0) {
            return Material.AIR;
        }
        return itemInHand.getType();
    }

    @Override
    public boolean isMastered() {

        return getAttachedLevel().hasReachedMaxLevel();
    }

    @Override
    public AttachedLevel<CharacterTemplate> getAttachedLevel() {

        return attachedLevel;
    }

    @Override
    public void attachLevel(AttachedLevel<CharacterTemplate> attachedLevel) {

        this.attachedLevel = attachedLevel;
    }

    @Override
    public int getMaxLevel() {

        return maxLevel;
    }

    @Override
    public void saveLevelProgress(AttachedLevel<CharacterTemplate> attachedLevel) {

        // override if needed
    }

    @Override
    public void onLevelLoss() {

        // override if needed
    }

    @Override
    public void onLevelGain() {

        // override if needed
    }

    @Override
    public void onExpLoss(int exp) {

        // override if needed
    }

    @Override
    public void onExpGain(int exp) {

        // override if needed
    }
}
