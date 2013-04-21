package de.raidcraft.skills.api.hero;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.tables.THero;
import de.raidcraft.skills.tables.THeroAttribute;
import de.raidcraft.skills.util.StringUtils;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public class ConfigurableAttribute implements Attribute {

    private final int id;
    private final Hero hero;
    private final String name;
    private final String friendlyName;
    private int value;

    public ConfigurableAttribute(Hero hero, String name, ConfigurationSection config) {

        this.hero = hero;
        this.name = StringUtils.formatName(name);
        this.friendlyName = config.getString("name", this.name);
        EbeanServer ebeanServer = RaidCraft.getDatabase(SkillsPlugin.class);
        THeroAttribute database = ebeanServer.find(THeroAttribute.class)
                .where()
                .eq("hero_id", hero.getId())
                .eq("attribute", this.name)
                .findUnique();
        if (database == null) {
            database = new THeroAttribute();
            database.setHero(ebeanServer.find(THero.class, hero.getId()));
            database.setAttribute(this.name);
            database.setValue(0);
            ebeanServer.save(database);
        }
        this.id = database.getId();
        this.value = database.getValue();
    }

    @Override
    public int getId() {

        return id;
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public String getFriendlyName() {

        return friendlyName;
    }

    @Override
    public Hero getHero() {

        return hero;
    }

    @Override
    public int getValue() {

        return value;
    }

    @Override
    public void setValue(int value) {

        this.value = value;
    }

    @Override
    public void save() {

        EbeanServer ebeanServer = RaidCraft.getDatabase(SkillsPlugin.class);
        THeroAttribute database = ebeanServer.find(THeroAttribute.class, getId());
        database.setValue(getValue());
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
