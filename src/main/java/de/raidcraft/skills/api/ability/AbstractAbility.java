package de.raidcraft.skills.api.ability;

import com.sk89q.minecraft.util.commands.CommandContext;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.ambient.AmbientEffect;
import de.raidcraft.api.ambient.CustomAmbientEffect;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectElement;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.combat.ProjectileType;
import de.raidcraft.skills.api.combat.action.AbilityAction;
import de.raidcraft.skills.api.combat.action.Attack;
import de.raidcraft.skills.api.combat.action.EntityAttack;
import de.raidcraft.skills.api.combat.action.HealAction;
import de.raidcraft.skills.api.combat.action.MagicalAttack;
import de.raidcraft.skills.api.combat.action.RangedAttack;
import de.raidcraft.skills.api.combat.callback.EntityAttackCallback;
import de.raidcraft.skills.api.combat.callback.ProjectileCallback;
import de.raidcraft.skills.api.effect.Effect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.hero.Option;
import de.raidcraft.skills.api.level.Levelable;
import de.raidcraft.skills.api.persistance.AbilityProperties;
import de.raidcraft.skills.api.skill.AbilityEffectStage;
import de.raidcraft.skills.api.skill.Passive;
import de.raidcraft.skills.effects.disabling.Disarm;
import de.raidcraft.skills.effects.disabling.Silence;
import de.raidcraft.skills.util.ConfigUtil;
import de.raidcraft.skills.util.HeroUtil;
import de.raidcraft.util.LocationUtil;
import de.raidcraft.util.TimeUtil;
import de.raidcraft.util.UUIDUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Silthus
 */
public abstract class AbstractAbility<T extends CharacterTemplate> implements Ability<T> {

    protected final T holder;
    protected final AbilityProperties properties;
    protected final Set<EffectType> effectTypes = new HashSet<>();
    protected final Set<EffectElement> effectElements = new HashSet<>();
    private final String name;
    // protected final THeroSkill database;
    protected String description;
    private Instant lastCast;
    private double cooldown;
    private BukkitTask cooldownInformTask;

    @SuppressWarnings("unchecked")
    public AbstractAbility(T holder, AbilityProperties data) {

        this.name = data.getName();
        this.description = data.getDescription();
        this.properties = data;
        this.holder = holder;
        this.effectTypes.addAll(getProperties().getTypes());
    }

    public final <E extends Effect<S>, S> void removeEffect(CharacterTemplate target, Class<E> eClass) throws CombatException {

        target.removeEffect(eClass, this);
    }

    @Override
    public final void checkUsage(AbilityAction<T> action) throws CombatException {

        if (!getProperties().getWorlds().isEmpty() && !getProperties().getWorlds().contains(getHolder().getEntity().getWorld().getName())) {
            throw new CombatException("Du kannst diesen Skill nicht in dieser Welt verwenden!");
        }
        if (this instanceof Passive) {
            throw new CombatException(CombatException.Type.PASSIVE);
        }
        if (getHolder().isInCombat() && !canUseInCombat()) {
            throw new CombatException(CombatException.Type.NO_COMBAT);
        }
        if (!getHolder().isInCombat() && !canUseOutOfCombat()) {
            throw new CombatException(CombatException.Type.COMBAT_ONLY);
        }
        // check common effects here
        if (this.isOfType(EffectType.MAGICAL) && getHolder().hasEffect(Silence.class)) {
            throw new CombatException(CombatException.Type.SILENCED);
        }
        if (this.isOfType(EffectType.PHYSICAL) && getHolder().hasEffect(Disarm.class)) {
            throw new CombatException(CombatException.Type.DISARMED);
        }
        if (isOnCooldown()) {
            throw new CombatException(CombatException.Type.ON_COOLDOWN.getMessage() +
                    " Noch: " +
                    (getRemainingCooldown() > 60.0 ? TimeUtil.secondsToMinutes(getRemainingCooldown()) + "min" : getRemainingCooldown() + "s"));
        }
    }

    public final <E extends Effect<S>, S> void removeEffect(Class<E> eClass) throws CombatException {

        getHolder().removeEffect(eClass, this);
    }

    @Override
    public final void substractUsageCost(AbilityAction<T> action) {

        // and lets set the cooldown because it is like a usage cost for further casting
        setLastCast(Instant.now());
        // get the cooldown from the skill action
        setCooldown(action.getCooldown(), false);
    }

    public final <E extends Effect<S>, S> boolean hasEffect(Class<E> eClass) {

        return getHolder().hasEffect(eClass, this);
    }

    @Override
    public boolean canUseAbility() {

        try {
            checkUsage(new AbilityAction<>(this));
            return true;
        } catch (CombatException ignored) {
        }
        return false;
    }

    public final <E extends Effect<S>, S> boolean hasEffect(CharacterTemplate target, Class<E> eClass) {

        return target.hasEffect(eClass, this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<AmbientEffect> getAmbientEffects(AbilityEffectStage stage) {

        List<AmbientEffect> effects = (List<AmbientEffect>) getProperties().getAmbientEffects().get(stage);
        if (effects == null) {
            return new ArrayList<>();
        }
        effects.stream()
                .filter(effect -> effect instanceof CustomAmbientEffect)
                .forEach(effect -> ((CustomAmbientEffect) effect).setEntity(getHolder().getEntity()));
        return effects;
    }

    @Override
    public List<AmbientEffect> getAmbientEffects(AbilityEffectStage stage, CharacterTemplate target) {

        List<AmbientEffect> effects = getAmbientEffects(stage);
        effects.stream()
                .filter(effect -> effect instanceof CustomAmbientEffect)
                .forEach(effect -> ((CustomAmbientEffect) effect).setTarget(target.getEntity()));
        return effects;
    }

    @Override
    public List<AmbientEffect> getAmbientEffects(AbilityEffectStage stage, Location target) {

        List<AmbientEffect> effects = getAmbientEffects(stage);
        effects.stream()
                .filter(effect -> effect instanceof CustomAmbientEffect)
                .forEach(effect -> ((CustomAmbientEffect) effect).setLocationTarget(target));
        return effects;
    }

    public final <E extends Effect<S>, S> E getEffect(CharacterTemplate target, Class<E> eClass) throws CombatException {

        return target.getEffect(eClass, this);
    }

    @Override
    public void executeAmbientEffects(AbilityEffectStage stage, Location location) {

        for (AmbientEffect effect : getAmbientEffects(stage)) {
            effect.run(location);
        }
    }

    public final <E extends Effect<S>, S> E getEffect(Class<E> eClass) throws CombatException {

        return getHolder().getEffect(eClass, this);
    }

    public final boolean matches(String name) {

        if (name == null) return false;
        name = name.toLowerCase();
        return getName().contains(name) || getFriendlyName().toLowerCase().contains(name);
    }

    public final <E extends Effect<S>, S> E addEffect(Class<E> eClass) throws CombatException {

        return addEffect(getHolder(), eClass);
    }

    @SuppressWarnings("unchecked")
    public final <E extends Effect<S>, S> E addEffect(CharacterTemplate target, Class<E> eClass) throws CombatException {

        return target.addEffect(this, (S) this, eClass);
    }

    public final <E extends Effect<S>, S> E addEffect(S source, CharacterTemplate target, Class<E> eClass) throws CombatException {

        return target.addEffect(this, source, eClass);
    }

    public List<CharacterTemplate> getNearbyTargets() throws CombatException {

        return getHolder().getNearbyTargets(getTotalRange());
    }

    public List<CharacterTemplate> getNearbyTargets(boolean friendly) throws CombatException {

        try {
            return getHolder().getNearbyTargets(getTotalRange(), friendly);
        } catch (CombatException e) {
            // lets check if this is an area effect
            if (!isOfType(EffectType.AREA) || !isOfType(EffectType.AURA)) {
                throw e;
            }
        }
        return new ArrayList<>();
    }

    public List<CharacterTemplate> getNearbyPartyMembers() {

        List<CharacterTemplate> targets = getNearbyFriendlyTargets();
        for (CharacterTemplate target : new ArrayList<>(targets)) {
            if (!target.isInParty(getHolder().getParty())) {
                targets.remove(target);
            }
        }
        return targets;
    }

    public List<CharacterTemplate> getNearbyFriendlyTargets() {

        return getSafeNearbyTargets(true);
    }

    public List<CharacterTemplate> getSafeNearbyTargets(boolean friendly) {

        try {
            return getNearbyTargets(friendly);
        } catch (CombatException e) {
            return new ArrayList<>();
        }
    }

    public List<CharacterTemplate> getSafeTargetsInFront() {

        try {
            return getTargetsInFront();
        } catch (CombatException e) {
            return new ArrayList<>();
        }
    }

    public List<CharacterTemplate> getTargetsInFront() throws CombatException {

        try {
            return getHolder().getTargetsInFront(getTotalRange());
        } catch (CombatException e) {
            // lets check if this is an area effect
            if (!isOfType(EffectType.AREA)) {
                throw e;
            }
        }
        return new ArrayList<>();
    }

    public List<CharacterTemplate> getSafeTargetsInFront(float degrees) {

        try {
            return getTargetsInFront(degrees);
        } catch (CombatException e) {
            return new ArrayList<>();
        }
    }

    public List<CharacterTemplate> getTargetsInFront(float degrees) throws CombatException {

        try {
            return getHolder().getTargetsInFront(getTotalRange(), degrees);
        } catch (CombatException e) {
            // lets check if this is an area effect
            if (!isOfType(EffectType.AREA)) {
                throw e;
            }
        }
        return new ArrayList<>();
    }

    public CharacterTemplate getTarget(CommandContext args, boolean friendlyOnly, boolean self) throws CombatException {

        CharacterTemplate target;
        if (args.argsLength() > 0) {

            target = RaidCraft.getComponent(SkillsPlugin.class).getCharacterManager().getHero(UUIDUtil.convertPlayer(args.getString(0)));
            if (!LocationUtil.isWithinRadius(getHolder().getEntity().getLocation(), target.getEntity().getLocation(), getTotalRange())) {
                throw new CombatException(CombatException.Type.OUT_OF_RANGE);
            }
        } else if (self
                || (getHolder().getEntity() instanceof Player
                && ((Player) getHolder().getEntity()).isSneaking())) {
            // self holy
            target = getHolder();
        } else {
            target = getTarget();
        }
        if (friendlyOnly && !target.isFriendly(getHolder())) {
            throw new CombatException("Du kannst nur freundliche Ziele anvisieren!");
        }
        return target;
    }

    public CharacterTemplate getTarget() throws CombatException {

        return getHolder().getTarget(getTotalRange());
    }

    public final Location getTargetBlock() throws CombatException {

        return getHolder().getBlockTarget(getTotalRange());
    }

    public final Attack<CharacterTemplate, CharacterTemplate> attack(CharacterTemplate target, double damage) throws CombatException {

        return attack(target, damage, null);
    }

    public final Attack<CharacterTemplate, CharacterTemplate> attack(CharacterTemplate target, double damage, EntityAttackCallback callback) throws CombatException {

        EntityAttack attack = new EntityAttack(getHolder(), target, damage, callback, getTypes().toArray(new EffectType[getTypes().size()]));
        attack.addAttackElement(getElements());
        return attack;
    }

    public final Attack<CharacterTemplate, CharacterTemplate> attack(CharacterTemplate target) throws CombatException {

        return attack(target, getTotalDamage(), null);
    }

    public final Attack<CharacterTemplate, CharacterTemplate> attack(CharacterTemplate target, EntityAttackCallback callback) throws CombatException {

        return attack(target, getTotalDamage(), callback);
    }

    public final HealAction<CharacterTemplate> heal(CharacterTemplate target) {

        return heal(target, getTotalDamage());
    }

    public final HealAction<CharacterTemplate> heal(CharacterTemplate target, double amount) {

        return new HealAction<>(getHolder(), target, amount);
    }

    public final <T extends ProjectileCallback> RangedAttack<T> rangedAttack(ProjectileType type) throws CombatException {

        return rangedAttack(type, getTotalDamage(), null);
    }

    public final <T extends ProjectileCallback> RangedAttack<T> rangedAttack(ProjectileType type, double damage, T callback) throws CombatException {

        RangedAttack<T> attack = new RangedAttack<>(getHolder(), type, damage, callback);
        attack.addAttackElement(getElements());
        return attack;
    }

    public final <T extends ProjectileCallback> RangedAttack<T> rangedAttack(ProjectileType type, double damage) throws CombatException {

        return rangedAttack(type, damage, null);
    }

    public final <T extends ProjectileCallback> RangedAttack<T> rangedAttack(ProjectileType type, T callback) throws CombatException {

        return rangedAttack(type, getTotalDamage(), callback);
    }

    public final MagicalAttack magicalAttack(EntityAttackCallback callback) throws CombatException {

        return magicalAttack(getTarget(), getTotalDamage(), callback);
    }

    public final MagicalAttack magicalAttack(CharacterTemplate target, double damage, EntityAttackCallback callback) throws CombatException {

        MagicalAttack magicalAttack = new MagicalAttack(getHolder(), target, damage, callback);
        magicalAttack.addAttackElement(getElements());
        magicalAttack.setImpactEffects(getAmbientEffects(AbilityEffectStage.IMPACT, target));
        magicalAttack.setLineEffects(getAmbientEffects(AbilityEffectStage.LINE, target));
        return magicalAttack;
    }

    public final MagicalAttack magicalAttack() throws CombatException {

        return magicalAttack(getTarget(), getTotalDamage(), null);
    }

    public final MagicalAttack magicalAttack(double damage) throws CombatException {

        return magicalAttack(damage, null);
    }

    public final MagicalAttack magicalAttack(double damage, EntityAttackCallback callback) throws CombatException {

        return magicalAttack(getTarget(), damage, callback);
    }

    public final MagicalAttack magicalAttack(CharacterTemplate target, EntityAttackCallback callback) throws CombatException {

        return magicalAttack(target, getTotalDamage(), callback);
    }

    public final MagicalAttack magicalAttack(CharacterTemplate taraget, double damage) throws CombatException {

        return magicalAttack(taraget, damage, null);
    }

    public final BlockFace getFacing() {

        return HeroUtil.yawToFace(getHolder().getEntity().getLocation().getYaw());
    }

    protected void warn(Throwable e) {

        warn(e.getMessage());
        RaidCraft.LOGGER.warning(e.getMessage());
        e.printStackTrace();
    }

    protected void warn(String message) {

        if (message == null || message.equals("")) {
            return;
        }
        if (getHolder() instanceof Hero) {
            warn((Hero) getHolder(), message);
        }
    }

    protected void warn(Hero hero, String message) {

        if (message == null || message.equals("")) {
            return;
        }
        hero.sendMessage(ChatColor.RED + message);
    }

    protected void info(String message) {

        if (message == null || message.equals("")) {
            return;
        }
        if (getHolder() instanceof Hero) {
            info((Hero) getHolder(), message);
        }
    }

    protected void info(Hero hero, String message) {

        if (message == null || message.equals("")) {
            return;
        }
        hero.sendMessage("" + ChatColor.GRAY + message);
    }

    protected void msg(String message) {

        if (message == null || message.equals("")) {
            return;
        }
        if (getHolder() instanceof Hero) {
            msg((Hero) getHolder(), message);
        }
    }

    protected void msg(Hero hero, String message) {

        if (message == null || message.equals("")) {
            return;
        }
        hero.sendMessage(message);
    }

    @Override
    public double getTotalDamage() {

        return ConfigUtil.getTotalValue(this, properties.getDamage());
    }

    protected void combatLog(String message) {

        if (message == null || message.equals("")) {
            return;
        }
        if (getHolder() instanceof Hero) {
            ((Hero) getHolder()).combatLog(this, message);
        }
    }

    @Override
    public final int getTotalRange() {

        return (int) ConfigUtil.getTotalValue(this, properties.getRange());
    }

    @Override
    public int hashCode() {

        return name.hashCode();
    }

    @Override
    public final long getConfiguredCooldown() {

        return Double.valueOf(ConfigUtil.getTotalValue(this, properties.getCooldown())).longValue();
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractAbility that = (AbstractAbility) o;

        return name.equals(that.name);

    }

    @Override
    public final double getTotalCastTime() {

        return ConfigUtil.getTotalValue(this, properties.getCastTime());
    }

    @Override
    public final String toString() {

        return getFriendlyName();
    }

    public double getCooldown() {

        if (this.cooldown <= 0) {
            this.cooldown = getConfiguredCooldown();
        }
        return this.cooldown;
    }

    @Override
    public final void setCooldown(double cooldown) {

        setCooldown(cooldown, true);
    }

    public final void setCooldown(double cooldown, boolean verbose) {

        double remainingCooldown = getRemainingCooldown();
        if (verbose && remainingCooldown != cooldown && getHolder() instanceof Hero) {
            if (cooldown > remainingCooldown) {
                ((Hero) getHolder()).sendMessage(
                        ChatColor.RED + "Cooldown von " + getFriendlyName() + " wurde auf " + cooldown + "s erhöht.");
            } else {
                ((Hero) getHolder()).sendMessage(
                        ChatColor.GREEN + "Cooldown von " + getFriendlyName() + " wurde auf " + cooldown + "s verringert.");
            }
        }
        this.cooldown = cooldown;
        if (cooldown > 0.0 && getHolder() instanceof Hero) {
            // start the cooldown task
            if (cooldownInformTask != null) {
                cooldownInformTask.cancel();
            }
            cooldownInformTask = Bukkit.getScheduler().runTaskLater(RaidCraft.getComponent(SkillsPlugin.class), new Runnable() {
                @Override
                public void run() {

                    if (Option.COMBAT_LOGGING.isSet((Hero) getHolder())) {
                        ((Hero) getHolder()).combatLog(AbstractAbility.this, getFriendlyName() + " ist wieder bereit.");
                    } else {
                        ((Hero) getHolder()).sendMessage(ChatColor.AQUA + getFriendlyName()
                                + ChatColor.GREEN + " ist wieder bereit.");
                    }
                }
            }, TimeUtil.secondsToTicks(cooldown));
        }
    }

    @Override
    public final double getRemainingCooldown() {

        if (getLastCast() == null) {
            return 0;
        }
        return getLastCast().plusSeconds((long) getCooldown()).getEpochSecond() - Instant.now().getEpochSecond();
    }

    @Override
    public final boolean isOnCooldown() {

        return getLastCast() != null && getLastCast().plusSeconds((long) getCooldown()).isAfter(Instant.now());
    }

    @Override
    public boolean isLevelable() {

        return this instanceof Levelable && properties.isLevelable();
    }

    @Override
    public Instant getLastCast() {

        return lastCast;
    }

    public final void setLastCast(Instant instant) {

        this.lastCast = instant;
    }

    @Override
    public void load(ConfigurationSection data) {
        // implement if needed
    }

    @Override
    public final String getName() {

        return name;
    }

    @Override
    public final String getFriendlyName() {

        return getProperties().getFriendlyName();
    }

    @Override
    public AbilityProperties getProperties() {

        return properties;
    }

    @Override
    public String getDescription() {

        return description
                .replace("%player%", holder.getName())
                .replace("%damage%", getTotalDamage() + "");
    }

    @Override
    public String[] getUsage() {

        return getProperties().getUsage();
    }

    @Override
    public final boolean canUseInCombat() {

        return properties.canUseInCombat();
    }

    @Override
    public final boolean canUseOutOfCombat() {

        return properties.canUseOutOfCombat();
    }

    @Override
    public final Set<EffectType> getTypes() {

        return effectTypes;
    }

    @Override
    public final void addTypes(EffectType... effectTypes) {

        if (effectTypes == null) return;
        this.effectTypes.addAll(Arrays.asList(effectTypes));
    }

    @Override
    public final boolean isOfType(EffectType type) {

        for (EffectType t : getTypes()) {
            if (t == type) {
                return true;
            }
        }
        return false;
    }

    @Override
    public final Set<EffectElement> getElements() {

        return effectElements;
    }

    @Override
    public final void addElements(EffectElement... effectElements) {

        if (effectElements == null) return;
        this.effectElements.addAll(Arrays.asList(effectElements));
    }

    @Override
    public final boolean isOfElement(EffectElement element) {

        for (EffectElement el : getElements()) {
            if (el == element) {
                return true;
            }
        }
        return false;
    }

    @Override
    public final T getHolder() {

        return holder;
    }

    @Override
    public void apply() {

        // override if needed
    }

    @Override
    public void remove() {

        // override if needed
    }


}
