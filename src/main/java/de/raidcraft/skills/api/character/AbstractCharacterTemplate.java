package de.raidcraft.skills.api.character;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.items.CustomArmor;
import de.raidcraft.api.items.CustomWeapon;
import de.raidcraft.api.items.EquipmentSlot;
import de.raidcraft.skills.CharacterManager;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.ability.Ability;
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
import de.raidcraft.skills.api.skill.AbilityEffectStage;
import de.raidcraft.skills.api.skill.EffectEffectStage;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.trigger.TriggerManager;
import de.raidcraft.skills.api.trigger.Triggered;
import de.raidcraft.skills.trigger.PlayerGainedEffectTrigger;
import de.raidcraft.util.BlockUtil;
import de.raidcraft.util.BukkitUtil;
import de.raidcraft.util.EffectUtil;
import de.raidcraft.util.LocationUtil;
import de.raidcraft.util.MathUtil;
import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;

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

    private final ThreatTable threatTable;
    private final Map<Class<? extends Effect>, Effect> effects = new HashMap<>();
    private final Map<EquipmentSlot, CustomWeapon> weapons = new EnumMap<>(EquipmentSlot.class);
    private final Map<EquipmentSlot, Long> lastSwing = new EnumMap<>(EquipmentSlot.class);
    private final Map<EquipmentSlot, CustomArmor> armorPieces = new EnumMap<>(EquipmentSlot.class);
    private String name;
    protected int maxLevel;
    // every player is member of his own party by default
    private Party party;
    private LivingEntity entity;
    private double damage;
    private boolean inCombat = false;
    private Attack lastAttack;
    private AttachedLevel<CharacterTemplate> attachedLevel;
    private boolean recalculateHealth = false;
    protected boolean usingHealthBar = true;

    public AbstractCharacterTemplate(LivingEntity entity) {

        this.entity = entity;
        this.threatTable = new ThreatTable(this);
        if (entity != null) {
            if (!usingHealthBar && entity.getCustomName() != null && !entity.getCustomName().equals("")) {
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

    protected void setName(String name) {

        getEntity().setCustomNameVisible(true);
        getEntity().setCustomName(name);
        this.name = ChatColor.stripColor(name);
    }

    @Override
    public void updateEntity(LivingEntity entity) {

        this.entity = entity;
    }

    @Override
    public LivingEntity getEntity() {

        return entity;
    }

    @Override
    public CharacterType getCharacterType() {

        return CharacterType.NATURAL;
    }

    @Override
    public ThreatTable getThreatTable() {

        return threatTable;
    }

    @Override
    public CustomWeapon getWeapon(EquipmentSlot slot) {

        return weapons.get(slot);
    }

    @Override
    public CustomWeapon removeWeapon(EquipmentSlot slot) {

        return weapons.remove(slot);
    }

    @Override
    public boolean hasWeapon(EquipmentSlot slot) {

        return weapons.containsKey(slot);
    }

    @Override
    public boolean hasWeaponsEquiped() {

        return !weapons.isEmpty();
    }

    @Override
    public void setWeapon(CustomWeapon weapon) {

        if (weapon.getEquipmentSlot() == EquipmentSlot.TWO_HANDED) {
            weapons.remove(EquipmentSlot.ONE_HANDED);
            weapons.remove(EquipmentSlot.SHIELD_HAND);
        }
        weapons.put(weapon.getEquipmentSlot(), weapon);
    }

    @Override
    public boolean canSwing(EquipmentSlot slot) {

        if (slot != EquipmentSlot.HANDS && this instanceof Hero) {
            CustomWeapon weapon = getWeapon(slot);
            if (weapon != null && !weapon.matches(getEntity().getEquipment().getItemInHand())) {
                return false;
            }
        }
        return System.currentTimeMillis() > getLastSwing(slot);
    }

    @Override
    public int swingWeapons() {

        int damage = 0;
        if (!hasWeaponsEquiped()) {
            damage += swingWeapon(EquipmentSlot.HANDS);
        } else {
            for (EquipmentSlot slot : weapons.keySet()) {
                damage += swingWeapon(slot);
            }
        }
        return damage;
    }

    @Override
    public int getWeaponDamage(EquipmentSlot slot) {

        if (!hasWeapon(slot)) {
            return 0;
        }
        CustomWeapon weapon = getWeapon(slot);
        if (weapon == null) {
            return 0;
        }
        return MathUtil.RANDOM.nextInt(weapon.getMaxDamage() - weapon.getMinDamage()) + weapon.getMinDamage();
    }

    @Override
    public int swingWeapon(EquipmentSlot slot) {

        if (slot != EquipmentSlot.HANDS && (!hasWeapon(slot) || !canSwing(slot))) {
            return 0;
        }
        setLastSwing(slot);
        return getWeaponDamage(slot);
    }

    @Override
    public long getLastSwing(EquipmentSlot slot) {

        if (!lastSwing.containsKey(slot)) {
            return 0;
        }
        return lastSwing.get(slot);
    }

    @Override
    public void setLastSwing(EquipmentSlot slot) {

        CustomWeapon weapon = getWeapon(slot);
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
    public void setArmor(CustomArmor armor) {

        armorPieces.put(armor.getEquipmentSlot(), armor);
        // if hero update the user interface
        if (this instanceof Hero) {
            ((Hero)this).getUserInterface().refresh();
        }
    }

    @Override
    public CustomArmor getArmor(EquipmentSlot slot) {

        return armorPieces.get(slot);
    }

    @Override
    public Collection<CustomArmor> getArmor() {

        return armorPieces.values();
    }

    @Override
    public CustomArmor removeArmor(EquipmentSlot slot) {

        CustomArmor remove = armorPieces.remove(slot);
        // if hero update the user interface
        if (this instanceof Hero) {
            ((Hero)this).getUserInterface().refresh();
        }
        return remove;
    }

    @Override
    public int getTotalArmorValue() {

        int armorValue = 0;
        for (CustomArmor armor : getArmor()) {
            armorValue += armor.getArmorValue();
        }
        return armorValue;
    }

    @Override
    public void clearArmor() {

        for (CustomArmor armor : getArmor()) {
            removeArmor(armor.getEquipmentSlot());
        }
        // if hero update the user interface
        if (this instanceof Hero) {
            ((Hero) this).getUserInterface().refresh();
        }
    }

    @Override
    public boolean hasArmor(EquipmentSlot slot) {

        return armorPieces.containsKey(slot);
    }

    @Override
    public void clearWeapons() {

        for (CustomWeapon weapon : getWeapons()) {
            removeWeapon(weapon.getEquipmentSlot());
        }
    }

    @Override
    public Attack getLastDamageCause() {

        return lastAttack;
    }

    @Override
    public Collection<CustomWeapon> getWeapons() {

        return weapons.values();
    }

    @Override
    public int getTotalWeaponDamage() {

        int damage = 0;
        for (EquipmentSlot slot : weapons.keySet()) {
            damage += getWeaponDamage(slot);
        }
        return damage;
    }

    @Override
    public boolean canAttack() {

        if (!(getEntity() instanceof Player)) return true;
        if (!hasWeaponsEquiped()) return canSwing(EquipmentSlot.HANDS);
        for (EquipmentSlot slot : weapons.keySet()) {
            if (canSwing(slot)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public double getDamage() {

        return this.damage;
    }

    @Override
    public void setDamage(double damage) {

        this.damage = damage;
    }

    @Override
    public void recalculateHealth() {

        if (isInCombat()) {
            recalculateHealth = true;
            return;
        }
        double maxHealth = getMaxHealth();
        double defaultHealth = getDefaultHealth();
        if (defaultHealth > maxHealth) {
            increaseMaxHealth(defaultHealth - maxHealth);
        } else if (defaultHealth < maxHealth) {
            decreaseMaxHealth(maxHealth - defaultHealth);
        }
        recalculateHealth = false;
    }

    @Override
    public double getHealth() {

        if (getEntity() == null) {
            return 0;
        }
        return (int) getEntity().getHealth();
    }

    @Override
    public void setHealth(double health) {

        if (getEntity() == null || getEntity().isDead()) {
            return;
        }
        if (health > getMaxHealth()) {
            health = getMaxHealth();
        }
        if (health < 0) {
            health = 0;
        }
        getEntity().setHealth(health);
    }

    @Override
    public double getMaxHealth() {

        if (getEntity() == null) {
            return 20;
        }
        return (int) getEntity().getMaxHealth();
    }

    @Override
    public void setMaxHealth(double maxHealth) {

        if (getEntity() == null) return;
        if (maxHealth < 1) {
            maxHealth = 20;
        }
        getEntity().setMaxHealth(maxHealth);
    }

    @Override
    public void increaseMaxHealth(double amount) {

        double newMaxHealth = getMaxHealth() + amount;
        setMaxHealth(newMaxHealth);
        setHealth(getHealth() + amount);
    }

    @Override
    public void decreaseMaxHealth(double amount) {

        setHealth((getHealth() - amount > 0 ? getHealth() - amount : 1));
        double newMaxHealth = getMaxHealth() - amount;
        setMaxHealth(newMaxHealth);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void damage(Attack attack) throws CombatException{

        if (getEntity().isDead()) {
            throw new CombatException(CombatException.Type.DEAD);
        }
        if (!attack.isCancelled() && attack.getDamage() > 0) {
            // lets get the actual attacker
            CharacterTemplate attacker = attack.getAttacker();
            // this all needs to happen before we damage the entity because of the events that are fired
            if (!(attack instanceof EnvironmentAttack)) {
                // lets add the combat effect
                if (attacker != null) attack.getAttacker().addEffect(this, Combat.class);
                addEffect(attack.getAttacker(), Combat.class);
                // set the last attack variable to track death
                lastAttack = attack;
                // lets increase the thread against the attacker
                if (attacker != null) {
                    if (getEntity() instanceof PigZombie) {
                        ((PigZombie) getEntity()).setAngry(true);
                    } else if (getEntity() instanceof Wolf) {
                        ((Wolf) getEntity()).setAngry(true);
                    }
                    getThreatTable().getThreatLevel(attacker).increaseThreat(attack.getThreat());
                }
            }
            // lets do some USK18+ gore effects
            // BLOOOOOOOOOOOOOOOOOOOD!!!!!!
            // 152 = redstone block
            EffectUtil.playEffect(getEntity().getLocation().add(0, 1, 0), org.bukkit.Effect.STEP_SOUND, 152, attack.getDamage() > 100 ? 5 : 1);
            if (attack.getSource() instanceof Ability) {
                ((Ability) attack.getSource()).executeAmbientEffects(AbilityEffectStage.DAMAGE, getEntity().getLocation());
            } else if (attack.getSource() instanceof Effect) {
                // lets also play the damage visual effects
                ((Effect) attack.getSource()).executeAmbientEffects(EffectEffectStage.DAMAGE, getEntity().getLocation());
            }
            // lets set some bukkit properties
            getEntity().setLastDamage(attack.getDamage());
            // also actually damage the entity
            int newHealth = (int) (getHealth() - attack.getDamage());
            if (newHealth <= 0) {
                kill();
            } else {
                setHealth(newHealth);
                getEntity().playEffect(EntityEffect.HURT);
                if (attack.hasKnockback()) {
                    if (attacker != null) {
                        getEntity().damage(0, attacker.getEntity());
                    } else {
                        getEntity().damage(0);
                    }
                }
            }
            // and some debug output
            if (attacker != null && attacker instanceof Hero) {
                ((Hero) attacker).combatLog("Du hast " + getName() +
                        (!(attack.getSource() instanceof CharacterTemplate) ? " mit " + attack.getSource() + " " : " ")
                        + attack.getDamage() + " Schaden zugefügt.");
            }
            if (this instanceof Hero) {
                ((Hero) this).combatLog("Du hast " + attack.getDamage() + " Schaden von " + attack.getSource() +
                        (attacker != null && attack.getSource() != attacker ? "[" + attacker.getName() + "] " : " ") + "erhalten.");
            }
        }
    }

    @Override
    public void heal(HealAction action) {

        if (getEntity().isDead()) {
            return;
        }
        double newHealth = getHealth() + action.getAmount();
        if (newHealth > getMaxHealth()) newHealth = getMaxHealth();
        // lets increase the threat
        if (action.getSource() instanceof CharacterTemplate && !(this instanceof Hero)) {
            getThreatTable().getThreatLevel((CharacterTemplate) action.getSource()).increaseThreat(action.getThreat());
        }
        getEntity().setNoDamageTicks(1);
        setHealth(newHealth);
        // lets fake some wolf hearts for visuals
        EffectUtil.fakeWolfHearts(getEntity().getLocation());
        Hero source = null;
        if (action.getSource() instanceof Hero) {
            source = (Hero) action.getSource();
        } else if (action.getSource() instanceof Skill) {
            source = ((Skill) action.getSource()).getHolder();
        } else if (action.getSource() instanceof Effect && ((Effect) action.getSource()).getSource() instanceof Skill) {
            source = ((Skill) ((Effect) action.getSource()).getSource()).getHolder();
        }
        // lets activate pvp on the source if the target had pvp activated
        if (source != null && !source.isPvPEnabled() && this instanceof Hero && ((Hero) this).isPvPEnabled()) {
            source.setPvPEnabled(true);
            source.sendMessage(ChatColor.RED + "Dein PvP Status wurde eingeschaltet da du ein freundliches PvP Ziel geheilt hast.");
        }
        if (source != null && this.equals(source)) {
            source.combatLog("Du hast dich um " + action.getAmount() + " Leben geheilt.");
        } else if (this instanceof Hero) {
            ((Hero) this).combatLog("Du wurdest von " + action.getSource() + " um " + action.getAmount() + "HP geheilt.");
        } else if (source != null) {
            source.combatLog("Du hast " + this + " um " + action.getAmount() + " geheilt.");
        }
    }

    @Override
    public void kill(CharacterTemplate killer) {

        if (getEntity().isDead()) {
            return;
        }
        RaidCraft.callEvent(new RCEntityDeathEvent(this));
        clearEffects();
        getEntity().damage(getMaxHealth(), killer.getEntity());
    }

    @Override
    public void kill() {

        if (getEntity().isDead()) {
            return;
        }
        getEntity().setCustomNameVisible(false);
        RaidCraft.callEvent(new RCEntityDeathEvent(this));
        clearEffects();
        getEntity().damage(getMaxHealth());
    }

    public <E extends Effect> void addEffect(Class<E> eClass, E effect) throws CombatException {

        // lets fire an event/trigger
        PlayerGainedEffectTrigger trigger = TriggerManager.callSafeTrigger(
                new PlayerGainedEffectTrigger(this, effect)
        );
        if (trigger.isCancelled()) {
            throw new CombatException(CombatException.Type.INVALID_TARGET);
        }

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

        if (nearbyEntities.size() < 1) throw new CombatException(CombatException.Type.OUT_OF_RANGE, "Keine Ziele in Reichweite von " + range + "m.");

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
        if (nearbyEntities.size() < 1) throw new CombatException(CombatException.Type.OUT_OF_RANGE, "Keine Ziele in Reichweite von " + range + "m.");
        for (LivingEntity target : nearbyEntities) {
            if (target.equals(getEntity())) {
                continue;
            }
            targets.add(RaidCraft.getComponent(SkillsPlugin.class).getCharacterManager().getCharacter(target));
        }
        return targets;
    }

    public List<CharacterTemplate> getNearbyTargets(int range, boolean friendly) throws CombatException {

        List<CharacterTemplate> nearbyTargets = getNearbyTargets(range);
        if (friendly) {
            List<CharacterTemplate> targets = new ArrayList<>();
            for (CharacterTemplate target : nearbyTargets) {
                if (target.isFriendly(this)) {
                    targets.add(target);
                }
            }
            // add self
            targets.add(this);
            return targets;
        } else {
            List<CharacterTemplate> targets = new ArrayList<>();
            for (CharacterTemplate target : nearbyTargets) {
                if (!target.isFriendly(this)) {
                    targets.add(target);
                }
            }
            return targets;
        }
    }

    @Override
    public CharacterTemplate getTarget() throws CombatException {

        return getTarget(100);
    }

    @Override
    public CharacterTemplate getTarget(int range) throws CombatException {

        LivingEntity target = BukkitUtil.getTargetEntity(getEntity(), LivingEntity.class);
        if (target == null) {
            throw new CombatException(CombatException.Type.INVALID_TARGET, "Du hast kein Ziel anvisiert!");
        }
        if (LocationUtil.getBlockDistance(target.getLocation(), getEntity().getLocation()) > range) {
            throw new CombatException(CombatException.Type.OUT_OF_RANGE, "Ziel ist nicht in Reichweite. Max. Reichweite: " + range + "m");
        }
        // check the line of sight between entities
        if (!getEntity().hasLineOfSight(target)) {
            throw new CombatException(CombatException.Type.INVALID_TARGET, "Ziel ist nicht im Sichtfeld.");
        }
        return RaidCraft.getComponent(SkillsPlugin.class).getCharacterManager().getCharacter(target);
    }

    @Override
    public Location getBlockTarget() throws CombatException {

        return getBlockTarget(100);
    }

    @Override
    public Location getBlockTarget(int range) throws CombatException {

        Block block = getEntity().getTargetBlock(BlockUtil.TRANSPARENT_BLOCKS, range);
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

        if (inCombat != this.inCombat) {
            CharacterManager.refreshPlayerTag(this);
        }
        this.inCombat = inCombat;
        if (!inCombat) {
            getThreatTable().reset();
            if (recalculateHealth) recalculateHealth();
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

        return source.equals(this) || getParty().isInGroup(source);
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

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof AbstractCharacterTemplate)) return false;

        AbstractCharacterTemplate that = (AbstractCharacterTemplate) o;

        return !(entity != null ? !entity.equals(that.entity) : that.entity != null);
    }

    @Override
    public int hashCode() {

        return entity != null ? entity.hashCode() : 0;
    }
}