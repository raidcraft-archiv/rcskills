package de.raidcraft.skills.api.hero;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.items.AttributeType;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.tables.THero;
import de.raidcraft.skills.tables.THeroAttribute;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public class ConfigurableAttribute implements Attribute {

    private final int id;
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
        EbeanServer ebeanServer = RaidCraft.getDatabase(SkillsPlugin.class);
        THeroAttribute database = ebeanServer.find(THeroAttribute.class)
                .where()
                .eq("hero_id", hero.getId())
                .eq("attribute", type)
                .findUnique();
        if (database == null) {
            database = new THeroAttribute();
            database.setHero(ebeanServer.find(THero.class, hero.getId()));
            database.setAttribute(type);
            database.setBaseValue(baseValue);
            database.setCurrentValue(baseValue);
            ebeanServer.save(database);
        }
        this.id = database.getId();
        this.baseValue = database.getBaseValue();
        this.currentValue = database.getCurrentValue();
    }

    @Override
    public int getId() {

        return id;
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
    }

    @Override
    public void save() {

        EbeanServer ebeanServer = RaidCraft.getDatabase(SkillsPlugin.class);
        THeroAttribute database = ebeanServer.find(THeroAttribute.class, getId());
        database.setBaseValue(getBaseValue());
        database.setCurrentValue(getCurrentValue());
        ebeanServer.save(database);
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConfigurableAttribute that = (ConfigurableAttribute) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {

        return id;
    }
}
