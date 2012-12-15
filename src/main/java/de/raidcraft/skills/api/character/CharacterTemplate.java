package de.raidcraft.skills.api.character;

import de.raidcraft.skills.api.combat.Effect;
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

    public void addEffect(Effect effect);

    public void removeEffect(Effect effect);

    public void clearEffects();

    public boolean hasEffect(Effect effect);

    public boolean hasEffectType(Effect.Type type);

    public boolean isInCombat();

    public void setInCombat(boolean inCombat);
}
