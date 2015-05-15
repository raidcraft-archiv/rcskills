package de.raidcraft.skills.api.hero;

import de.raidcraft.api.items.AttributeType;
import de.raidcraft.skills.api.combat.EffectType;

/**
 * @author Silthus
 */
public interface Attribute {

    String getName();

    String getFriendlyName();

    AttributeType getType();

    Hero getHero();

    int getBaseValue();

    void setBaseValue(int value);

    int getCurrentValue();

    void setCurrentValue(int value);

    void addValue(int value);

    void removeValue(int value);

    double getBonusDamage(EffectType type);
}
