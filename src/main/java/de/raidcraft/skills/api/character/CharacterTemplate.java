package de.raidcraft.skills.api.character;

import de.raidcraft.api.items.CustomArmor;
import de.raidcraft.api.items.CustomWeapon;
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
import java.util.UUID;

/**
 * @author Silthus
 */
public interface CharacterTemplate extends Levelable<CharacterTemplate> {

    public default UUID getUniqueId() {

        return getEntity().getUniqueId();
    }

    public String getName();

    public void updateEntity(LivingEntity entity);

    public LivingEntity getEntity();

    public CharacterType getCharacterType();

    public ThreatTable getThreatTable();

    public Combat getLastCombat();

    public Attack getLastDamageCause();

    @Nullable
    public Action<? extends CharacterTemplate> getLastAction();

    public void setLastAction(Action<? extends CharacterTemplate> action);

    public CustomWeapon getWeapon(EquipmentSlot slot);

    public Collection<CustomWeapon> getWeapons();

    public boolean hasWeapon(EquipmentSlot slot);

    public boolean hasWeaponsEquiped();

    public void setWeapon(CustomWeapon weapon);

    public CustomWeapon removeWeapon(EquipmentSlot slot);

    public void clearWeapons();

    boolean canAttack();

    public int getWeaponDamage(EquipmentSlot slot);

    public int getTotalWeaponDamage();

    public boolean canSwing(EquipmentSlot slot);

    public int swingWeapons();

    public int swingWeapon(EquipmentSlot slot);

    public long getLastSwing(EquipmentSlot slot);

    public void setLastSwing(EquipmentSlot slot);

    Collection<CustomArmor> getArmor();

    void setArmor(CustomArmor armorPiece);

    CustomArmor getArmor(EquipmentSlot slot);

    CustomArmor removeArmor(EquipmentSlot type);

    int getTotalArmorValue();

    void clearArmor();

    boolean hasArmor(EquipmentSlot slot);

    Party getParty();

    boolean isInParty(Party party);

    void joinParty(Party party);

    void leaveParty();

    public double getDamage();

    public void setDamage(double damage);

    public void attachHealthDisplay(HealthDisplay display);

    public void removeHealthDisplay(HealthDisplay display);

    public void recalculateHealth();

    public double getHealth();

    public void setHealth(double health);

    public double getMaxHealth();

    public void setMaxHealth(double maxHealth);

    public void increaseMaxHealth(double amount);

    public void decreaseMaxHealth(double amount);

    public double getDefaultHealth();

    public void damage(Attack attack) throws CombatException;

    public void heal(HealAction action);

    public default void remove() {
        kill();
    }

    public void kill(CharacterTemplate killer);

    public void kill();

    public void setLastKill(CharacterTemplate lastKill);

    @Nullable
    public CharacterTemplate getLastKill();

    public boolean isFriendly(CharacterTemplate source);

    public boolean isBehind(CharacterTemplate target);

    public <E extends Effect> void addEffect(Class<E> eClass, E effect) throws CombatException;

    public <E extends Effect<S>, S> E addEffect(Ability ability, S source, Class<E> eClass) throws CombatException;

    public <E extends Effect<S>, S> E addEffect(S source, Class<E> eClass) throws CombatException;

    public <E> void removeEffect(Class<E> eClass) throws CombatException;

    public <E> void removeEffect(Class<E> eClass, Object source) throws CombatException;

    public void removeEffect(Effect effect) throws CombatException;

    public <E extends Effect> boolean hasEffect(Class<E> eClass);

    public <E extends Effect> boolean hasEffect(Class<E> eClass, Object source);

    public <E extends Effect> List<E> getEffects(Class<E> eClass);

    public <E extends Effect> E getEffect(Class<E> eClass, Object source);

    public boolean hasEffectType(EffectType type);

    public void removeEffectTypes(EffectType type) throws CombatException;

    public List<Effect> getEffects();

    public List<Effect> getEffects(EffectType... type);

    public void clearEffects();

    public boolean isInCombat();

    public void setInCombat(boolean inCombat);

    public void triggerCombat(Object source) throws CombatException;

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
}
