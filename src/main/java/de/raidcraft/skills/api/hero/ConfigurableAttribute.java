package de.raidcraft.skills.api.hero;

import de.raidcraft.api.items.AttributeType;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public class ConfigurableAttribute implements Attribute {

    private final Hero hero;
    private final AttributeType type;
    private final double damageModifier;
    private final double healthModifier;
    private int baseValue;
    private int currentValue;

    public ConfigurableAttribute(Hero hero, String name, int baseValue, ConfigurationSection config) {

        this.hero = hero;
        this.type = AttributeType.fromString(name);
        this.damageModifier = config.getDouble("damage-modifier", 0.0);
        this.healthModifier = config.getDouble("health-modifier", 0.0);
        this.baseValue = baseValue;
        this.currentValue = baseValue;
    }

    @Override
    public String getName() {

        return getType().name().toLowerCase();
    }

    @Override
    public String getFriendlyName() {

        return getType().getGermanName();
    }

    @Override
    public AttributeType getType() {

        return type;
    }

    @Override
    public double getDamageModifier() {

        return damageModifier;
    }

    @Override
    public double getHealthModifier() {

        return healthModifier;
    }

    @Override
    public Hero getHero() {

        return hero;
    }

    @Override
    public int getBaseValue() {

        return baseValue;
    }

    @Override
    public void setBaseValue(int value) {

        this.baseValue = value;
        hero.recalculateHealth();
    }

    @Override
    public int getCurrentValue() {

        return currentValue;
    }

    @Override
    public void setCurrentValue(int currentValue) {

        if (currentValue < baseValue) {
            currentValue = baseValue;
        }
        this.currentValue = currentValue;
        hero.recalculateHealth();
    }

    @Override
    public void addValue(int value) {

        setCurrentValue(getCurrentValue() + value);
    }

    @Override
    public void removeValue(int value) {

        setCurrentValue(getCurrentValue() - value);
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConfigurableAttribute that = (ConfigurableAttribute) o;

        return type == that.type;

    }

    @Override
    public int hashCode() {

        return type.hashCode();
    }
}
