package de.raidcraft.skills.api.resource;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.util.EnumUtils;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Set;

/**
 * @author Silthus
 */
public interface Resource {

    public enum Type {

        PERCENTAGE,
        FLAT;

        public static Type fromString(String name) {

            return EnumUtils.getEnumFromString(Type.class, name);
        }
    }

    public Hero getHero();

    public Profession getProfession();

    public ConfigurationSection getConfig();

    public Set<VisualResourceType> getTypes();

    public String getName();

    public String getFriendlyName();

    public double getDefault();

    public double getMin();

    public boolean isMin();

    public double getCurrent();

    public void setCurrent(double current);

    public double getBaseValue();

    public double getPercentage();

    public double getMax();

    public boolean isMax();

    public boolean isEnabled();

    public void setEnabled(boolean enabled);

    public long getRegenInterval();

    public void setRegenInterval(long interval);

    public double getRegenValue();

    public void setRegenValue(double percent);

    public void regen();

    public void destroy();

    public void save();
}
