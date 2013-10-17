package de.raidcraft.skills.api.hero;

import de.raidcraft.api.items.AttributeType;
import de.raidcraft.skills.api.combat.EffectType;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public class ConfigurableAttribute implements Attribute {

    private final Hero hero;
    private final AttributeType type;
    private final Map<EffectType, Double> damageModifier = new HashMap<>();
    private int baseValue;
    private int currentValue;

    public ConfigurableAttribute(Hero hero, String name, int baseValue, ConfigurationSection config) {

        this.hero = hero;
        this.type = AttributeType.fromString(name);
        this.baseValue = baseValue;
        this.currentValue = baseValue;
        ConfigurationSection section = config.getConfigurationSection("damage-modifiers");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                EffectType effectType = EffectType.fromString(key);
                if (effectType != null) {
                    damageModifier.put(effectType, section.getDouble(key));
                }
            }
        }
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
    public double getBonusDamage(EffectType type) {

        if (!damageModifier.containsKey(type)) {
            return 0;
        }
        return damageModifier.get(type) * getCurrentValue();
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
