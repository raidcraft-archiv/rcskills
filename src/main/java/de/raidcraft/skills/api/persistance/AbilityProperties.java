package de.raidcraft.skills.api.persistance;

import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public interface AbilityProperties<T> {

    String getName();

    String getFriendlyName();

    String getDescription();

    String[] getUsage();

    boolean isEnabled();

    void setEnabled(boolean enabled);

    boolean canUseInCombat();

    boolean canUseOutOfCombat();

    ConfigurationSection getData();

    T getInformation();

    ConfigurationSection getDamage();

    ConfigurationSection getCastTime();

    ConfigurationSection getRange();

    ConfigurationSection getCooldown();
}
