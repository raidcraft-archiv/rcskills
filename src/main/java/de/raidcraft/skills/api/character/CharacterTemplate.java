package de.raidcraft.skills.api.character;

import de.raidcraft.skills.api.EffectType;
import de.raidcraft.skills.api.combat.attack.Attack;
import de.raidcraft.skills.api.combat.effect.Effect;
import de.raidcraft.skills.api.exceptions.CombatException;
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

    public void kill(CharacterTemplate killer);

    public void kill();

    public <S> Effect addEffect(S source, Class<? extends Effect> eClass) throws CombatException;

    public void removeEffect(Class<? extends Effect<?>> eClass) throws CombatException;

    public void removeEffect(Effect effect) throws CombatException;

    public boolean hasEffect(Class<? extends Effect<?>> eClass);

    public boolean hasEffectType(EffectType type);

    public void clearEffects();

    public boolean isInCombat();

    public void setInCombat(boolean inCombat);
}
