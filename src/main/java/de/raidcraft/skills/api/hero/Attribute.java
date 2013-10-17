package de.raidcraft.skills.api.hero;

import de.raidcraft.api.items.AttributeType;
import de.raidcraft.skills.api.combat.EffectType;

/**
 * @author Silthus
 */
public interface Attribute {

    public String getName();

    public String getFriendlyName();

    public AttributeType getType();

    public Hero getHero();

    public void setBaseValue(int value);

    public int getBaseValue();

    public int getCurrentValue();

    public void setCurrentValue(int value);

    public void addValue(int value);

    public void removeValue(int value);

    public double getBonusDamage(EffectType type);
}
