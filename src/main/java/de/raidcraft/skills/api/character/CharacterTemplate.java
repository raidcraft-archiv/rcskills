package de.raidcraft.skills.api.character;

import de.raidcraft.api.items.CustomItemStack;
import de.raidcraft.api.items.EquipmentSlot;
import de.raidcraft.skills.api.ability.Ability;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.combat.ThreatTable;
import de.raidcraft.skills.api.combat.action.Action;
import de.raidcraft.skills.api.combat.action.Attack;
import de.raidcraft.skills.api.combat.action.HealAction;
import de.raidcraft.skills.api.effect.Effect;
import de.raidcraft.skills.api.effect.common.Combat;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.level.Levelable;
import de.raidcraft.skills.api.party.Party;
import de.raidcraft.skills.api.ui.HealthDisplay;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Silthus
 */
public interface CharacterTemplate extends Levelable<CharacterTemplate> {

    default UUID getUniqueId() {

        return getEntity().getUniqueId();
    }

    Optional<CharacterTemplate> getKiller();

    String getName();

    void updateEntity(LivingEntity entity);

    LivingEntity getEntity();

    CharacterType getCharacterType();

    ThreatTable getThreatTable();

    Combat getLastCombat();

    Attack getLastDamageCause();

    @Nullable
    Action<? extends CharacterTemplate> getLastAction();

    void setLastAction(Action<? extends CharacterTemplate> action);

    CustomItemStack getWeapon(EquipmentSlot slot);

    Collection<CustomItemStack> getWeapons();

    boolean hasWeapon(EquipmentSlot slot);

    boolean hasWeaponsEquiped();

    boolean setWeapon(CustomItemStack weapon);

    CustomItemStack removeWeapon(EquipmentSlot slot);

    void clearWeapons();

    boolean canAttack();

    int getWeaponDamage(EquipmentSlot slot);

    int getTotalWeaponDamage();

    boolean canSwing(EquipmentSlot slot);

    int swingWeapons();

    int swingWeapon(EquipmentSlot slot);

    long getLastSwing(EquipmentSlot slot);

    void setLastSwing(EquipmentSlot slot);

    Collection<CustomItemStack> getArmor();

    boolean setArmor(CustomItemStack armorPiece);

    CustomItemStack getArmor(EquipmentSlot slot);

    CustomItemStack removeArmor(EquipmentSlot type);

    int getTotalArmorValue();

    void clearArmor();

    boolean hasArmor(EquipmentSlot slot);

    Party getParty();

    boolean isInParty(Party party);

    void joinParty(Party party);

    void leaveParty();

    double getDamage();

    void setDamage(double damage);

    void attachHealthDisplay(HealthDisplay display);

    void removeHealthDisplay(HealthDisplay display);

    void recalculateHealth();

    double getHealth();

    void setHealth(double health);

    double getMaxHealth();

    void setMaxHealth(double maxHealth);

    void increaseMaxHealth(double amount);

    void decreaseMaxHealth(double amount);

    double getDefaultHealth();

    void damage(Attack attack) throws CombatException;

    void heal(HealAction action);

    void heal(double amount) throws CombatException;

    void heal(double amount, String source) throws CombatException;

    default void remove() {
        kill();
    }

    /**
     * Kills the character by damaging him with his {@link #getMaxHealth()}.
     * Can be cancelled by cancelling the {@link de.raidcraft.skills.api.events.RCEntityDeathEvent}.
     *
     * @param killer of the character
     * @return true if the character was killed, false if the kill was cancelled
     */
    boolean kill(CharacterTemplate killer);

    /**
     * Kills the character by damaging him with his {@link #getMaxHealth()}.
     * Can be cancelled by cancelling the {@link de.raidcraft.skills.api.events.RCEntityDeathEvent}.
     *
     * @return true if the character was killed, false if the kill was cancelled
     */
    boolean kill();

    void setLastKill(CharacterTemplate lastKill);

    @Nullable
    CharacterTemplate getLastKill();

    boolean isFriendly(CharacterTemplate source);

    boolean isBehind(CharacterTemplate target);

    <E extends Effect> void addEffect(Class<E> eClass, E effect) throws CombatException;

    <E extends Effect<S>, S> E addEffect(Ability ability, S source, Class<E> eClass) throws CombatException;

    <E extends Effect<S>, S> E addEffect(S source, Class<E> eClass) throws CombatException;

    <E> void removeEffect(Class<E> eClass) throws CombatException;

    <E> void removeEffect(Class<E> eClass, Object source) throws CombatException;

    void removeEffect(Effect effect) throws CombatException;

    <E extends Effect> boolean hasEffect(Class<E> eClass);

    <E extends Effect> boolean hasEffect(Class<E> eClass, Object source);

    <E extends Effect> List<E> getEffects(Class<E> eClass);

    <E extends Effect> E getEffect(Class<E> eClass, Object source);

    Optional<Effect> getEffect(String name);

    boolean hasEffectType(EffectType type);

    void removeEffectTypes(EffectType type) throws CombatException;

    List<Effect> getEffects();

    List<Effect> getEffects(EffectType... type);

    void clearEffects();

    boolean isInCombat();

    void setInCombat(boolean inCombat);

    void triggerCombat(Object source) throws CombatException;

    CharacterTemplate getTarget(int range) throws CombatException;

    Location getBlockTarget(int range) throws CombatException;

    Location getBlockTarget() throws CombatException;

    CharacterTemplate getTarget() throws CombatException;

    List<CharacterTemplate> getNearbyTargets() throws CombatException;

    List<CharacterTemplate> getNearbyTargets(int range) throws CombatException;

    List<CharacterTemplate> getNearbyTargets(int range, boolean friendly) throws CombatException;

    default List<CharacterTemplate> getNearbyTargets(int range, boolean friendly, boolean self) throws CombatException {

        List<CharacterTemplate> nearbyTargets = getNearbyTargets(range, friendly);
        if (self) nearbyTargets.add(this);
        return nearbyTargets;
    }

    List<CharacterTemplate> getTargetsInFront(int range, float degrees) throws CombatException;

    List<CharacterTemplate> getTargetsInFront(int range) throws CombatException;

    List<CharacterTemplate> getTargetsInFront() throws CombatException;

    Material getItemTypeInHand();

    void reset();

    List<CharacterTemplate> getInvolvedTargets();
}
