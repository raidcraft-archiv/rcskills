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

    Hero getHero();

    Profession getProfession();

    ConfigurationSection getConfig();

    Set<VisualResourceType> getTypes();

    String getName();

    String getFriendlyName();

    double getDefault();

    double getMin();

    boolean isMin();

    double getCurrent();

    void setCurrent(double current);

    double getBaseValue();

    double getPercentage();

    double getMax();

    boolean isMax();

    boolean isRegenEnabled();

    void setRegenEnabled(boolean enabled);

    boolean isEnabled();

    void setEnabled(boolean enabled);

    long getRegenInterval();

    void setRegenInterval(long interval);

    double getRegenValue();

    void setRegenValue(double percent);

    long getRegenUseageDelay();

    void setRegenUseageDelay(long delay);

    void regen();

    void destroy();

    void save();

    enum Type {

        PERCENTAGE,
        FLAT;

        public static Type fromString(String name) {

            return EnumUtils.getEnumFromString(Type.class, name);
        }
    }
}
