package de.raidcraft.skills.api.character;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.items.*;
import de.raidcraft.skills.CharacterManager;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.ability.Ability;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.combat.ThreatTable;
import de.raidcraft.skills.api.combat.action.Action;
import de.raidcraft.skills.api.combat.action.Attack;
import de.raidcraft.skills.api.combat.action.EnvironmentAttack;
import de.raidcraft.skills.api.combat.action.HealAction;
import de.raidcraft.skills.api.effect.Effect;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.Stackable;
import de.raidcraft.skills.api.effect.common.Combat;
import de.raidcraft.skills.api.events.RCEntityDeathEvent;
import de.raidcraft.skills.api.events.RCMaxHealthChangeEvent;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.AttachedLevel;
import de.raidcraft.skills.api.party.Party;
import de.raidcraft.skills.api.party.SimpleParty;
import de.raidcraft.skills.api.skill.AbilityEffectStage;
import de.raidcraft.skills.api.skill.EffectEffectStage;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.trigger.TriggerManager;
import de.raidcraft.skills.api.ui.HealthDisplay;
import de.raidcraft.skills.trigger.AttackTrigger;
import de.raidcraft.skills.trigger.DamageTrigger;
import de.raidcraft.skills.trigger.PlayerGainedEffectTrigger;
import de.raidcraft.util.*;
import lombok.AccessLevel;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Silthus
 */
public abstract class AbstractCharacterTemplate implements CharacterTemplate {

    private final ThreatTable threatTable;
    private final Map<Class<? extends Effect>, Map<Object, Effect>> effects = new HashMap<>();
    private final Map<EquipmentSlot, CustomItemStack> weapons = new EnumMap<>(EquipmentSlot.class);
    private final Map<EquipmentSlot, Long> lastSwing = new EnumMap<>(EquipmentSlot.class);
    private final Map<EquipmentSlot, CustomItemStack> armorPieces = new EnumMap<>(EquipmentSlot.class);
    private final Set<HealthDisplay> healthDisplays = new HashSet<>();
    protected int maxLevel;
    protected boolean usingHealthBar = true;
    private String name;
    // every player is member of his own party by default
    private Party party;
    private LivingEntity entity;
    private double damage;
    private boolean inCombat = false;
    private Attack lastDamageCause;
    private Action<? extends CharacterTemplate> lastAction;
    private Combat lastCombat;
    private AttachedLevel<CharacterTemplate> attachedLevel;
    private boolean recalculateHealth = false;
    private CharacterTemplate lastKill;
    @Setter(AccessLevel.PROTECTED)
    private CharacterTemplate killer = null;

    public AbstractCharacterTemplate(LivingEntity entity) {

        this.entity = entity;
        this.threatTable = new ThreatTable(this);
        if (entity != null) {
            if (!usingHealthBar && entity.getCustomName() != null && !entity.getCustomName().equals("")) {
                this.name = entity.getCustomName();
            } else if (entity instanceof Player) {
                this.name = entity.getName();
            } else {
                this.name = entity.getType().getName();
            }
        } else {
            this.name = "UNKNOWN";
        }
        this.party = new SimpleParty(this);
    }

    @Override
    public Optional<CharacterTemplate> getKiller() {
        return Optional.ofNullable(killer);
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
                return Sound.ENTITY_COW_AMBIENT;
            case BLAZE:
                return Sound.ENTITY_BLAZE_DEATH;
            case CHICKEN:
                return Sound.ENTITY_CHICKEN_HURT;
            case CREEPER:
                return Sound.ENTITY_CREEPER_DEATH;
            case SKELETON:
                return Sound.ENTITY_SKELETON_DEATH;
            case GHAST:
                return Sound.ENTITY_GHAST_DEATH;
            case PIG:
                return Sound.ENTITY_PIG_DEATH;
            case OCELOT:
                return Sound.ENTITY_CAT_HURT;
            case SHEEP:
                return Sound.ENTITY_SHEEP_AMBIENT;
            case SPIDER:
            case CAVE_SPIDER:
                return Sound.ENTITY_SPIDER_DEATH;
            case WOLF:
                return Sound.ENTITY_WOLF_DEATH;
            case ZOMBIE:
                return Sound.ENTITY_ZOMBIE_DEATH;
            default:
                return Sound.ENTITY_GENERIC_DEATH;
        }
    }

    @Override
    public int hashCode() {

        return entity != null ? entity.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof AbstractCharacterTemplate)) return false;

        AbstractCharacterTemplate that = (AbstractCharacterTemplate) o;

        return !(entity != null ? !entity.equals(that.entity) : that.entity != null);
    }

    @Override
    public String toString() {

        return getName();
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
    public Combat getLastCombat() {

        return lastCombat;
    }

    @Override
    public Attack getLastDamageCause() {

        return lastDamageCause;
    }

    @Override
    public void setLastAction(Action<? extends CharacterTemplate> action) {

        this.lastAction = action;
    }

    @Override
    public Action<? extends CharacterTemplate> getLastAction() {

        return lastAction;
    }

    @Override
    public CustomItemStack getWeapon(EquipmentSlot slot) {

        return weapons.get(slot);
    }

    @Override
    public Collection<CustomItemStack> getWeapons() {

        return weapons.values();
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
    public boolean setWeapon(CustomItemStack customItem) {

        if (customItem == null) return false;
        CustomItem item = customItem.getItem();
        if (item instanceof CustomWeapon) {
            CustomWeapon weapon = (CustomWeapon) item;
            CustomItemStack currentWeapon = weapons.get(weapon.getEquipmentSlot());
            if (currentWeapon != null && currentWeapon.getItem().equals(weapon)) {
                return false;
            }
            if (weapon.getEquipmentSlot() == EquipmentSlot.TWO_HANDED) {
                removeWeapon(EquipmentSlot.ONE_HANDED);
                removeWeapon(EquipmentSlot.SHIELD_HAND);
            }
            removeWeapon(weapon.getEquipmentSlot());
            weapons.put(weapon.getEquipmentSlot(), customItem);
            return true;
        }
        return false;
    }

    @Override
    public CustomItemStack removeWeapon(EquipmentSlot slot) {

        return weapons.remove(slot);
    }

    @Override
    public void clearWeapons() {

        getWeapons().stream()
                .filter(weapon -> weapon.getItem() instanceof CustomWeapon)
                .forEach(weapon -> removeWeapon(((CustomWeapon) weapon.getItem()).getEquipmentSlot()));
    }

    @Override
    public boolean canAttack() {

        // TODO: check if this fixes #824
        // if (!(getEntity() instanceof Player)) return true;
        if (!hasWeaponsEquiped()) return canSwing(EquipmentSlot.HANDS);
        for (EquipmentSlot slot : weapons.keySet()) {
            if (canSwing(slot)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getWeaponDamage(EquipmentSlot slot) {

        if (!hasWeapon(slot)) {
            return 0;
        }
        CustomItemStack weapon = getWeapon(slot);
        if (weapon == null || !(weapon.getItem() instanceof CustomWeapon)) {
            return 0;
        }
        CustomWeapon customWeapon = (CustomWeapon) weapon.getItem();
        return MathUtil.RANDOM.nextInt(customWeapon.getMaxDamage() - customWeapon.getMinDamage()) + customWeapon.getMinDamage();
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
    public boolean canSwing(EquipmentSlot slot) {

        if (slot != EquipmentSlot.HANDS && this instanceof Hero) {
            CustomItemStack weapon = getWeapon(slot);
            if (weapon != null && !weapon.getItem().matches(getEntity().getEquipment().getItemInHand())) {
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
    public int swingWeapon(EquipmentSlot slot) {

        setLastSwing(slot);
        if (slot != EquipmentSlot.HANDS && (!hasWeapon(slot) || !canSwing(slot))) {
            return 0;
        }
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

        CustomItemStack weapon = getWeapon(slot);
        long swingTime = 1000;
        if (weapon != null && weapon.getItem() instanceof CustomWeapon) {
            swingTime *= ((CustomWeapon) weapon.getItem()).getSwingTime();
        } else {
            swingTime *= RaidCraft.getComponent(SkillsPlugin.class).getCommonConfig().default_swing_time;
        }
        long lastSwing = System.currentTimeMillis() + swingTime;
        this.lastSwing.put(slot, lastSwing);
    }

    @Override
    public Collection<CustomItemStack> getArmor() {

        return armorPieces.values();
    }

    @Override
    public boolean setArmor(CustomItemStack item) {

        CustomItem customItem = item.getItem();
        if (customItem instanceof CustomArmor) {
            CustomItemStack customItemStack = armorPieces.get(((CustomArmor) customItem).getEquipmentSlot());
            if (customItemStack != null && customItemStack.getItem().equals(customItem)) return false;
            removeArmor(((CustomArmor) customItem).getEquipmentSlot());
            armorPieces.put(((CustomArmor) customItem).getEquipmentSlot(), item);
            // if hero update the user interface
            if (this instanceof Hero) {
                ((Hero) this).getUserInterface().refresh();
            }
            return true;
        }
        return false;
    }

    @Override
    public CustomItemStack getArmor(EquipmentSlot slot) {

        return armorPieces.get(slot);
    }

    @Override
    public CustomItemStack removeArmor(EquipmentSlot slot) {

        CustomItemStack remove = armorPieces.remove(slot);
        // if hero update the user interface
        if (this instanceof Hero) {
            ((Hero) this).getUserInterface().refresh();
        }
        return remove;
    }

    @Override
    public int getTotalArmorValue() {

        int armorValue = 0;
        for (CustomItemStack armor : getArmor()) {
            if (armor.getItem() instanceof CustomArmor) {
                armorValue += ((CustomArmor) armor.getItem()).getArmorValue();
            }
        }
        return armorValue;
    }

    @Override
    public void clearArmor() {

        for (CustomItemStack armor : getArmor()) {
            if (armor.getItem() instanceof CustomArmor) {
                removeArmor(((CustomArmor) armor.getItem()).getEquipmentSlot());
            }
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
    public Party getParty() {

        return party;
    }

    @Override
    public boolean isInParty(Party party) {

        return party.contains(this);
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
    public double getDamage() {

        return this.damage;
    }

    @Override
    public void setDamage(double damage) {

        this.damage = damage;
    }

    @Override
    public void attachHealthDisplay(HealthDisplay display) {

        healthDisplays.add(display);
        display.refresh();
    }

    @Override
    public void removeHealthDisplay(HealthDisplay display) {

        if (healthDisplays.remove(display)) {
            display.remove();
        }
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
        // lets update all attached health displays
        for (HealthDisplay display : healthDisplays) {
            display.refresh();
        }
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

        RCMaxHealthChangeEvent event = new RCMaxHealthChangeEvent(this, amount);
        RaidCraft.callEvent(event);
        double newMaxHealth = getMaxHealth() + event.getValue();
        setMaxHealth(newMaxHealth);
        setHealth(getHealth() + event.getValue());  
    }

    @Override
    public void decreaseMaxHealth(double amount) {

        RCMaxHealthChangeEvent event = new RCMaxHealthChangeEvent(this, -amount);
        RaidCraft.callEvent(event);
        // we use plus here because we inverted the amount to a negative value above
        setHealth((getHealth() + event.getValue() > 0 ? getHealth() + event.getValue() : 1));
        double newMaxHealth = getMaxHealth() + event.getValue();
        setMaxHealth(newMaxHealth);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void damage(Attack attack) throws CombatException {

        if (getEntity().isDead()) {
            throw new CombatException(CombatException.Type.DEAD);
        }
        // lets run the triggers first to give the skills a chance to cancel the attack or do what not
        // call the attack trigger
        if (attack.getTarget() == null || !(attack.getTarget() instanceof CharacterTemplate)) {
            attack.setTarget(this);
        }
        CharacterTemplate attacker = attack.getAttacker();
        if (attacker != null) {
            AttackTrigger attackTrigger = new AttackTrigger(attacker, attack, attack.getCause());
            TriggerManager.callTrigger(attackTrigger);
            if (attackTrigger.isCancelled()) attack.setCancelled(true);
        }
        // call the damage trigger
        DamageTrigger damageTrigger = new DamageTrigger(this, attack, attack.getCause());
        TriggerManager.callTrigger(damageTrigger);
        if (damageTrigger.isCancelled()) attack.setCancelled(true);

        if (!attack.isCancelled() && attack.getDamage() > 0) {
            // lets get the actual attacker
            // this all needs to happen before we damage the entity because of the events that are fired
            if (!(attack instanceof EnvironmentAttack)) {
                // lets add the combat effect
                if (attacker != null) attacker.addEffect(this, Combat.class);
                addEffect(attacker, Combat.class);
                // set the last attack variable to track death
                lastDamageCause = attack;
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
            if (attack.isOfAttackType(EffectType.CRITICAL)) {
                EffectUtil.playEffect(getEntity().getLocation().add(0, 1, 0), org.bukkit.Effect.MOBSPAWNER_FLAMES, 1, 50);
            }
            // lets set some bukkit properties
            getEntity().setLastDamage(attack.getDamage());
            // also actually damage the entity
            int newHealth = (int) (getHealth() - attack.getDamage());
            if (newHealth <= 0) {
                kill(attacker);
            } else {
                setHealth(newHealth);
                getEntity().playEffect(EntityEffect.HURT);
                if (attacker != null && getEntity() instanceof Monster) {
                    ((Monster) getEntity()).setTarget(attacker.getEntity());
                }
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
                ((Hero) attacker).combatLog("Du hast " + getName() + " (" + getAttachedLevel().getLevel() + ")" +
                        (!(attack.getSource() instanceof CharacterTemplate) ? " mit " + attack.getSource() + " " : " ")
                        + (int) attack.getDamage()
                        + (attack.isOfAttackType(EffectType.CRITICAL) ? " KRITISCHEN" : "")
                        + " Schaden zugefügt.");
            }
            if (this instanceof Hero) {
                ((Hero) this).combatLog((attacker != null ? "[" + attacker.getName() + " ("
                        + attacker.getAttachedLevel().getLevel() + ")" + "] hat dir " : "Dir wurde  ")
                        + (int) attack.getDamage()
                        + (attack.isOfAttackType(EffectType.CRITICAL) ? " KRITISCHEN" : "")
                        + " Schaden" + (!(attack.getSource() instanceof CharacterTemplate) ? " mit "
                        + attack.getSource() : "") + " zugefügt.");
            }
        }
    }

    @Override
    public void heal(double amount) throws CombatException {
        heal(amount, null);
    }

    @Override
    public void heal(double amount, String source) throws CombatException {
        new HealAction<>(source, this, amount).run();
    }

    @Override
    public void heal(HealAction action) {

        if (getEntity() == null || getEntity().isDead()) {
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
            if (action.getSource() instanceof String) {
                ((Hero) this).combatLog("Du wurdest von " + action.getSource() + " um " + action.getAmount() + "HP geheilt.");
            } else {
                source.combatLog("Du hast " + this + " um " + action.getAmount() + " geheilt.");
            }
        }
    }

    @Override
    public boolean kill(CharacterTemplate killer) {

        if (getEntity().isDead()) {
            return false;
        }
        RCEntityDeathEvent event = new RCEntityDeathEvent(this);
        RaidCraft.callEvent(event);

        if (event.isCancelled()) return false;

        clearEffects();
        getEntity().setCustomNameVisible(false);
        if (killer != null) {
            killer.setLastKill(this);
        }
        setKiller(killer);
        // we need to damage not set health the entity or else it wont fire an death event
        getEntity().damage(getMaxHealth());

        return true;
    }

    @Override
    public boolean kill() {

        return kill(null);
    }

    @Nullable
    @Override
    public CharacterTemplate getLastKill() {

        return lastKill;
    }

    @Override
    public void setLastKill(CharacterTemplate lastKill) {

        this.lastKill = lastKill;
    }

    @Override
    public boolean isFriendly(CharacterTemplate source) {

        return source.equals(this) || getParty().contains(source);
    }

    @Override
    public boolean isBehind(CharacterTemplate target) {

        // we asume that if the target cannot see us we are behind it
        return target.getEntity().hasLineOfSight(getEntity());
    }

    public <E extends Effect> void addEffect(Class<E> eClass, E effect) throws CombatException {

        // lets fire an event/trigger
        PlayerGainedEffectTrigger trigger = TriggerManager.callSafeTrigger(
                new PlayerGainedEffectTrigger(this, effect)
        );
        if (trigger.isCancelled()) {
            throw new CombatException(CombatException.Type.INVALID_TARGET);
        }

        if (!effects.containsKey(eClass)) {
            effects.put(eClass, new HashMap<>());
        }

        boolean globalEffect = eClass.getAnnotation(EffectInformation.class).global();
        if (hasEffect(eClass, effect.getSource()) || (globalEffect && hasEffect(eClass))) {
            Effect<?> existingEffect;
            if (globalEffect) {
                existingEffect = effects.get(eClass).values().stream().findFirst().orElseGet(() -> effect);
            } else {
                existingEffect = effects.get(eClass).get(effect.getSource());
            }
            // lets check priorities
            if (existingEffect instanceof Stackable) {
                // we dont replace or renew stackable effects, we increase their stacks :)
                existingEffect.setStacks(existingEffect.getStacks() + 1);
                if (existingEffect instanceof Combat) {
                    lastCombat = (Combat) existingEffect;
                }
                return;
            } else if (existingEffect.getPriority() < 0) {
                // prio less then 0 is special and means always replace
                existingEffect.remove();
                addEffect(eClass, effect);
            } else if (existingEffect.getPriority() > effect.getPriority()) {
                throw new CombatException("Es ist bereits ein stärkerer Effekt aktiv!");
            } else if (existingEffect.getPriority() == effect.getPriority()) {
                // lets renew the existing effect
                existingEffect.renew();
                if (existingEffect instanceof Combat) {
                    lastCombat = (Combat) existingEffect;
                }
                return;
            } else {
                // the new effect has a higher priority so lets remove the old one
                existingEffect.remove();
                addEffect(eClass, effect);
            }
        } else {
            // apply the new effect
            effects.get(eClass).put(effect.getSource(), effect);
            effect.apply();
        }
        if (effect instanceof Combat) {
            lastCombat = (Combat) effect;
        }
    }

    @Override
    public <E extends Effect<S>, S> E addEffect(Ability ability, S source, Class<E> eClass) throws CombatException {

        E effect = RaidCraft.getComponent(SkillsPlugin.class).getEffectManager().getEffect(source, this, eClass, ability);
        addEffect(eClass, effect);
        return effect;
    }

    @Override
    public <E extends Effect<S>, S> E addEffect(Ability ability, S source, Class<E> eClass, Consumer<E> afterLoad) throws CombatException {
        E effect = RaidCraft.getComponent(SkillsPlugin.class).getEffectManager().getEffect(source, this, eClass, ability);
        afterLoad.accept(effect);
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
        if (getLastCombat() != null && source instanceof CharacterTemplate) {
            getLastCombat().addInvolvedCharacter((CharacterTemplate) source);
        }
        return effect;
    }

    @Override
    public <E> void removeEffect(Class<E> eClass) throws CombatException {

        Map<Object, Effect> effects = this.effects.getOrDefault(eClass, new HashMap<>());
        if (!effects.isEmpty()) {
            for (Effect effect : new ArrayList<>(effects.values())) {
                Effect removedEffect = effects.remove(effect.getSource());
                if (removedEffect != null) removedEffect.remove();
            }
        }
    }

    @Override
    public <E> void removeEffect(Class<E> eClass, Object source) throws CombatException {

        if (eClass.getAnnotation(EffectInformation.class).global()) {
            removeEffect(eClass);
            return;
        }
        Effect<?> effect = effects.getOrDefault(eClass, new HashMap<>()).remove(source);
        if (effect != null) {
            effect.remove();
        }
    }

    @Override
    public void removeEffect(Effect effect) throws CombatException {

        removeEffect(effect.getClass(), effect.getSource());
    }

    @Override
    public <E extends Effect> boolean hasEffect(Class<E> eClass) {

        return effects.containsKey(eClass) && !effects.get(eClass).isEmpty();
    }

    @Override
    public <E extends Effect> boolean hasEffect(Class<E> eClass, Object source) {

        return hasEffect(eClass) && effects.get(eClass).containsKey(source);
    }

    @Override
    public <E extends Effect> List<E> getEffects(Class<E> eClass) {

        return this.effects.get(eClass).values().stream()
                .map(eClass::cast)
                .collect(Collectors.toList());
    }

    @Nullable
    public <E extends Effect> E getGlobalEffect(Class<E> eClass) {

        return getEffect(eClass, null);
    }

    @Override
    @Nullable
    public <E extends Effect> E getEffect(Class<E> eClass, Object source) {

        Map<Object, Effect> effects = this.effects.getOrDefault(eClass, new HashMap<>());
        if (eClass.getAnnotation(EffectInformation.class).global()) {
            return (E) effects.values().stream().findAny().orElseGet(null);
        }
        if (effects.containsKey(source)) {
            return (E) effects.get(source);
        }
        if (effects.size() == 1) {
            Optional<Effect> first = effects.values().stream().findFirst();
            if (first.isPresent()) {
                return (E) first.get();
            }
        }
        return null;
    }

    @Override
    public Optional<Effect> getEffect(String name) {
        List<Effect> effects = this.effects.values().stream()
                .flatMap(map -> map.values().stream())
                .filter(effect -> effect.getName().equalsIgnoreCase(name) || effect.getFriendlyName().equalsIgnoreCase(name))
                .collect(Collectors.toList());
        if (effects.size() == 1) return Optional.of(effects.get(0));
        return Optional.empty();
    }

    @Override
    public final boolean hasEffectType(EffectType type) {

        return effects.values().stream()
                .anyMatch(entry -> entry.values().stream()
                        .anyMatch(effect -> effect.isOfType(type)));
    }

    @Override
    public final void removeEffectTypes(EffectType type) {

        List<Effect> effectsToRemove = new ArrayList<>();
        for (Map<Object, Effect> effectMap : effects.values()) {
            effectMap.values().stream().filter(effect -> effect.isOfType(type)).forEach(effectsToRemove::add);
        }
        effectsToRemove.forEach(effect -> {
            try {
                effect.remove();
            } catch (CombatException e) {
                if (effect.getTarget() instanceof Hero) {
                    ((Hero) effect.getTarget()).sendMessage(ChatColor.RED + e.getMessage());
                }
            }
        });
    }

    @Override
    public final List<Effect> getEffects() {

        List<Effect> effects = new ArrayList<>();
        this.effects.values().forEach(entry -> effects.addAll(entry.values()));
        return effects;
    }

    @Override
    public final List<Effect> getEffects(EffectType... types) {

        List<Effect> effects = new ArrayList<>();
        this.effects.values().forEach(entry -> entry.values().stream()
                .filter(effect -> effect.isOfAnyType(types))
                .forEach(effects::add));
        return effects;
    }

    @Override
    public final void clearEffects() {

        for (Map<Object, Effect> entry : effects.values()) {
            new ArrayList<>(entry.values()).forEach(effect -> {
                try {
                    if (effect != null) {
                        effect.remove();
                    }
                } catch (CombatException e) {
                    if (effect.getTarget() instanceof Hero) {
                        ((Hero) effect.getTarget()).sendMessage(ChatColor.RED + e.getMessage());
                    }
                }
            });
        }
        effects.clear();
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
    public void triggerCombat(Object source) throws CombatException {

        addEffect(source, Combat.class);
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
    public Location getBlockTarget(int range) throws CombatException {

        Block block = getEntity().getTargetBlock(BlockUtil.TRANSPARENT_BLOCKS, range);
        if (block == null
                || LocationUtil.getBlockDistance(block.getLocation(), getEntity().getLocation()) > range) {
            throw new CombatException("Ziel ist nicht in Reichweite. Max. Reichweite: " + range + "m");
        }
        return block.getLocation();
    }

    @Override
    public Location getBlockTarget() throws CombatException {

        return getBlockTarget(100);
    }

    @Override
    public CharacterTemplate getTarget() throws CombatException {

        return getTarget(100);
    }

    @Override
    public List<CharacterTemplate> getNearbyTargets() throws CombatException {

        return getNearbyTargets(30);
    }

    @Override
    public List<CharacterTemplate> getNearbyTargets(int range) throws CombatException {

        List<CharacterTemplate> targets = new ArrayList<>();
        List<LivingEntity> nearbyEntities = BukkitUtil.getNearbyEntities(getEntity(), range);
        if (nearbyEntities.size() < 1) {
            throw new CombatException(CombatException.Type.OUT_OF_RANGE, "Keine Ziele in Reichweite von " + range + "m.");
        }
        for (LivingEntity target : nearbyEntities) {
            if (target.equals(getEntity())) {
                continue;
            }
            CharacterTemplate character = RaidCraft.getComponent(SkillsPlugin.class).getCharacterManager().getCharacter(target);
            if (character != null) {
                targets.add(character);
            }
        }
        return targets;
    }

    public List<CharacterTemplate> getNearbyTargets(int range, boolean friendly) throws CombatException {

        return getNearbyTargets(range).stream()
                .filter(target -> friendly == target.isFriendly(this))
                .collect(Collectors.toList());
    }

    @Override
    public List<CharacterTemplate> getNearbyTargets(int range, boolean friendly, boolean self) throws CombatException {

        List<CharacterTemplate> targets = getNearbyTargets(range, friendly);
        if (self) targets.add(this);
        return targets;
    }

    @Override
    public List<CharacterTemplate> getTargetsInFront(int range, float degrees) throws CombatException {

        List<CharacterTemplate> targets = new ArrayList<>();
        List<LivingEntity> nearbyEntities = BukkitUtil.getLivingEntitiesInCone(getEntity(), range, degrees);

        if (nearbyEntities.size() < 1) {
            throw new CombatException(CombatException.Type.OUT_OF_RANGE, "Keine Ziele in Reichweite von " + range + "m.");
        }

        targets.addAll(nearbyEntities.stream()
                .map(target -> RaidCraft.getComponent(SkillsPlugin.class).getCharacterManager().getCharacter(target))
                .collect(Collectors.toList()));

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
    public Material getItemTypeInHand() {

        ItemStack itemInHand = getEntity().getEquipment().getItemInHand();
        if (itemInHand == null) {
            return Material.AIR;
        }
        return itemInHand.getType();
    }

    @Override
    public boolean isMastered() {

        return getAttachedLevel().hasReachedMaxLevel();
    }

    @Override
    public void reset() {

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
    @SuppressWarnings("unchecked")
    public List<CharacterTemplate> getInvolvedTargets() {

        Combat combat = getLastCombat();
        if (combat == null) {
            return new ArrayList<>();
        }
        Set<CharacterTemplate> characters = combat.getInvolvedCharacters();
        characters.add(getLastDamageCause().getAttacker());
        return new ArrayList<>(characters);
    }
}