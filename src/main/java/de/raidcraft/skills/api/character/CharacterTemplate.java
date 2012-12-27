package de.raidcraft.skills.api.character;

import de.raidcraft.skills.api.EffectType;
import de.raidcraft.skills.api.combat.attack.Attack;
import de.raidcraft.skills.api.effect.Effect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.entity.LivingEntity;

/**
 * @author Silthus
 */
public interface CharacterTemplate {

    public String getName();

    public LivingEntity getEntity();

    public int getHealth();

    public void setHealth(int health);

    public int getMaxHealth();

    public void damage(int damage);

    public void damage(Attack attack);

    public void heal(int amount);

    public void kill(CharacterTemplate killer);

    public void kill();

    public <E extends Effect> E addEffect(Skill skill, Object source, Class<E> eClass) throws CombatException;

    public <E extends Effect> E addEffect(Object source, Class<E> eClass) throws CombatException;

    public void removeEffect(Class<? extends Effect<?>> eClass) throws CombatException;

    public void removeEffect(Effect effect) throws CombatException;

    public boolean hasEffect(Class<? extends Effect> eClass);

    public boolean hasEffectType(EffectType type);

    public void clearEffects();

    public boolean isInCombat();

    public void setInCombat(boolean inCombat);
}
