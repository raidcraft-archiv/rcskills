package de.raidcraft.skills.api.character;

import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.combat.action.Attack;
import de.raidcraft.skills.api.effect.Effect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.party.Party;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.items.ArmorPiece;
import de.raidcraft.skills.items.ArmorType;
import de.raidcraft.skills.items.Weapon;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import java.util.Collection;
import java.util.List;

/**
 * @author Silthus
 */
public interface CharacterTemplate {

    public String getName();

    public LivingEntity getEntity();

    public Weapon getWeapon(Weapon.Slot slot);

    public boolean hasWeapon(Weapon.Slot slot);

    public void setWeapon(Weapon weapon);

    public void removeWeapon(Weapon.Slot slot);

    public boolean canSwing(Weapon.Slot slot);

    public long getLastSwing(Weapon.Slot slot);

    public void setLastSwing(Weapon.Slot slot);

    Collection<ArmorPiece> getArmor();

    ArmorPiece getArmor(ArmorType slot);

    void setArmor(ArmorPiece armorPiece);

    void removeArmor(ArmorType type);

    void clearArmor();

    boolean hasArmor(ArmorType slot);

    Party getParty();

    boolean isInParty(Party party);

    void joinParty(Party party);

    void leaveParty();

    public int getDamage();

    public void setDamage(int damage);

    public int getHealth();

    public void setHealth(int health);

    public int getMaxHealth();

    public void setMaxHealth(int maxHealth);

    public int getDefaultHealth();

    public void damage(Attack attack);

    public void heal(int amount);

    public void kill(CharacterTemplate killer);

    public void kill();

    public boolean isFriendly(CharacterTemplate source);

    public boolean isBehind(CharacterTemplate target);

    public <E extends Effect> void addEffect(Class<E> eClass, E effect) throws CombatException;

    public <E extends Effect<S>, S> E addEffect(Skill skill, S source, Class<E> eClass) throws CombatException;

    public <E extends Effect<S>, S> E addEffect(S source, Class<E> eClass) throws CombatException;

    public <E> void removeEffect(Class<E> eClass) throws CombatException;

    public void removeEffect(Effect effect) throws CombatException;

    public <E extends Effect> boolean hasEffect(Class<E> eClass);

    public <E extends Effect> E getEffect(Class<E> eClass);

    public boolean hasEffectType(EffectType type);

    public void removeEffectTypes(EffectType type) throws CombatException;

    public List<Effect> getEffects();

    public List<Effect> getEffects(EffectType... type);

    public void clearEffects();

    public boolean isInCombat();

    public void setInCombat(boolean inCombat);

    CharacterTemplate getTarget(int range) throws CombatException;

    Location getBlockTarget(int range) throws CombatException;

    Location getBlockTarget() throws CombatException;

    CharacterTemplate getTarget() throws CombatException;

    List<CharacterTemplate> getNearbyTargets() throws CombatException;

    List<CharacterTemplate> getNearbyTargets(int range) throws CombatException;

    List<CharacterTemplate> getNearbyTargets(int range, boolean friendly) throws CombatException;

    List<CharacterTemplate> getTargetsInFront(int range, float degrees) throws CombatException;

    List<CharacterTemplate> getTargetsInFront(int range) throws CombatException;

    List<CharacterTemplate> getTargetsInFront() throws CombatException;
}
