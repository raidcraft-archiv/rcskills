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

    /**
     * Updates the attributes base value with or without updating the current value.
     *
     * @param value to increase or decrease base value with. Use negative numbers to decrease the the value. Cannot be below 0.
     * @param updateCurrentValue set to true to increase the current value by the same amount after updating the base value
     */
    void updateBaseValue(int value, boolean updateCurrentValue);

    void addValue(int value);

    void removeValue(int value);

    double getBonusDamage(EffectType type);
}
