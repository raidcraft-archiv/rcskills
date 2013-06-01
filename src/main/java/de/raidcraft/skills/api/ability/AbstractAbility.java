package de.raidcraft.skills.api.ability;

import com.sk89q.minecraft.util.commands.CommandContext;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.ambient.AmbientEffect;
import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectElement;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.combat.ProjectileType;
import de.raidcraft.skills.api.combat.action.Attack;
import de.raidcraft.skills.api.combat.action.EntityAttack;
import de.raidcraft.skills.api.combat.action.MagicalAttack;
import de.raidcraft.skills.api.combat.action.RangedAttack;
import de.raidcraft.skills.api.combat.callback.EntityAttackCallback;
import de.raidcraft.skills.api.combat.callback.ProjectileCallback;
import de.raidcraft.skills.api.effect.Effect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.AbilityProperties;
import de.raidcraft.skills.api.skill.AbilityEffectStage;
import de.raidcraft.skills.util.ConfigUtil;
import de.raidcraft.skills.util.HeroUtil;
import de.raidcraft.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Silthus
 */
public abstract class AbstractAbility<T extends CharacterTemplate> implements Ability<T> {

    private final String name;
    protected final T holder;
    protected final AbilityProperties properties;
    protected final Set<EffectType> effectTypes = new HashSet<>();
    protected final Set<EffectElement> effectElements = new HashSet<>();
    // protected final THeroSkill database;
    protected String description;
    private long lastCast;

    public AbstractAbility(T holder, AbilityProperties data) {

        this.name = data.getName();
        this.description = data.getDescription();
        this.properties = data;
        this.holder = holder;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<AmbientEffect> getAmbientEffects(AbilityEffectStage stage) {

        Object effects = getProperties().getAmbientEffects().get(stage);
        if (effects == null) {
            return new ArrayList<>();
        }
        return (List<AmbientEffect>) effects;
    }

    public final boolean matches(String name) {

        name = name.toLowerCase();
        return getName().contains(name) || getFriendlyName().toLowerCase().contains(name);
    }

    @SuppressWarnings("unchecked")
    public final <E extends Effect<S>, S> E addEffect(CharacterTemplate target, Class<E> eClass) throws CombatException {

        return target.addEffect(this, (S) this, eClass);
    }

    public final <E extends Effect<S>, S> E addEffect(S source, CharacterTemplate target, Class<E> eClass) throws CombatException {

        return target.addEffect(this, source, eClass);
    }

    public List<CharacterTemplate> getNearbyTargets() throws CombatException {

        return getNearbyTargets(true);
    }

    public List<CharacterTemplate> getNearbyTargets(boolean friendly) throws CombatException {

        return getHolder().getNearbyTargets(getTotalRange(), friendly);
    }

    public List<CharacterTemplate> getTargetsInFront() throws CombatException {

        return getHolder().getTargetsInFront(getTotalRange());
    }

    public List<CharacterTemplate> getTargetsInFront(float degrees) throws CombatException {

        return getHolder().getTargetsInFront(getTotalRange(), degrees);
    }

    public CharacterTemplate getTarget() throws CombatException {

        return getHolder().getTarget(getTotalRange());
    }

    public CharacterTemplate getTarget(CommandContext args, boolean friendlyOnly, boolean self) throws CombatException {

        CharacterTemplate target;
        if (args.argsLength() > 0) {
            try {
                target = RaidCraft.getComponent(SkillsPlugin.class).getCharacterManager().getHero(args.getString(0));
                if (!LocationUtil.isWithinRadius(getHolder().getEntity().getLocation(), target.getEntity().getLocation(), getTotalRange())) {
                    throw new CombatException(CombatException.Type.OUT_OF_RANGE);
                }
            } catch (UnknownPlayerException e) {
                throw new CombatException(e.getMessage());
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
            throw new CombatException("Du kannst nur dich selbst oder Gruppenmitglieder anvisieren.");
        }
        return target;
    }

    public final Location getBlockTarget() throws CombatException {

        return getHolder().getBlockTarget(getTotalRange());
    }

    public final Attack<CharacterTemplate, CharacterTemplate> attack(CharacterTemplate target, int damage) throws CombatException {

        return attack(target, damage, null);
    }

    public final Attack<CharacterTemplate, CharacterTemplate> attack(CharacterTemplate target) throws CombatException {

        return attack(target, getTotalDamage(), null);
    }

    public final Attack<CharacterTemplate, CharacterTemplate> attack(CharacterTemplate target, EntityAttackCallback callback) throws CombatException {

        return attack(target, getTotalDamage(), callback);
    }

    public final Attack<CharacterTemplate, CharacterTemplate> attack(CharacterTemplate target, int damage, EntityAttackCallback callback) throws CombatException {

        EntityAttack attack = new EntityAttack(getHolder(), target, damage, callback, getTypes().toArray(new EffectType[getTypes().size()]));
        attack.run();
        return attack;
    }

    public final <T extends ProjectileCallback> RangedAttack<T> rangedAttack(ProjectileType type) throws CombatException {

        return rangedAttack(type, getTotalDamage(), null);
    }

    public final <T extends ProjectileCallback> RangedAttack<T> rangedAttack(ProjectileType type, int damage) throws CombatException {

        return rangedAttack(type, damage, null);
    }

    public final <T extends ProjectileCallback> RangedAttack<T> rangedAttack(ProjectileType type, T callback) throws CombatException {

        return rangedAttack(type, getTotalDamage(), callback);
    }

    public final <T extends ProjectileCallback> RangedAttack<T> rangedAttack(ProjectileType type, int damage, T callback) throws CombatException {

        RangedAttack<T> attack = new RangedAttack<>(getHolder(), type, damage, callback);
        attack.run();
        return attack;
    }

    public final MagicalAttack magicalAttack(CharacterTemplate target, int damage, EntityAttackCallback callback) throws CombatException {

        MagicalAttack magicalAttack = new MagicalAttack(getHolder(), target, damage, callback);
        magicalAttack.setImpactEffects(getAmbientEffects(AbilityEffectStage.IMPACT));
        magicalAttack.setLineEffects(getAmbientEffects(AbilityEffectStage.LINE));
        magicalAttack.run();
        return magicalAttack;
    }

    public final MagicalAttack magicalAttack(int damage, EntityAttackCallback callback) throws CombatException {

        return magicalAttack(getTarget(), damage, callback);
    }

    public final MagicalAttack magicalAttack(EntityAttackCallback callback) throws CombatException {

        return magicalAttack(getTarget(), getTotalDamage(), callback);
    }

    public final MagicalAttack magicalAttack() throws CombatException {

        return magicalAttack(getTarget(), getTotalDamage(), null);
    }

    public final MagicalAttack magicalAttack(int damage) throws CombatException {

        return magicalAttack(damage, null);
    }

    public final MagicalAttack magicalAttack(CharacterTemplate target, EntityAttackCallback callback) throws CombatException {

        return magicalAttack(target, getTotalDamage(), callback);
    }

    public final MagicalAttack magicalAttack(CharacterTemplate taraget, int damage) throws CombatException {

        return magicalAttack(taraget, damage, null);
    }

    public final BlockFace getFacing() {

        return HeroUtil.yawToFace(getHolder().getEntity().getLocation().getYaw());
    }

    @Override
    public final int getTotalDamage() {

        return (int) ConfigUtil.getTotalValue(this, properties.getDamage());
    }

    @Override
    public final int getTotalRange() {

        return (int) ConfigUtil.getTotalValue(this, properties.getRange());
    }

    @Override
    public final double getTotalCooldown() {

        return ConfigUtil.getTotalValue(this, properties.getCooldown());
    }

    @Override
    public final int getTotalCastTime() {

        return (int) (ConfigUtil.getTotalValue(this, properties.getCastTime()) * 20.0);
    }

    @Override
    public final void setRemainingCooldown(double cooldown) {

        setLastCast((long) (System.currentTimeMillis() - cooldown * 1000));
    }

    @Override
    public final long getRemainingCooldown() {

        return (long) ((lastCast + (getTotalCooldown() * 1000)) - System.currentTimeMillis());
    }

    @Override
    public final boolean isOnCooldown() {

        return getRemainingCooldown() > 0;
    }

    @Override
    public final void setLastCast(long time) {

        this.lastCast = time;
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

    @Override
    public final String toString() {

        return getFriendlyName();
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractAbility that = (AbstractAbility) o;

        return name.equals(that.name);

    }

    @Override
    public int hashCode() {

        return name.hashCode();
    }
}
