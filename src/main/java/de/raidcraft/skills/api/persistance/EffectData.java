package de.raidcraft.skills.api.persistance;

import de.raidcraft.skills.api.effect.EffectInformation;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public interface EffectData {

    public String getFriendlyName();

    public String getActivateMessage();

    public String getDeactivateMessage();

    public String getRenewMessage();

    public EffectInformation getInformation();

    public boolean isEnabled();

    public ConfigurationSection getDataMap();

    public int getMaxStacks();

    public double getEffectPriority();

    public ConfigurationSection getEffectDuration();

    public ConfigurationSection getEffectDelay();

    public ConfigurationSection getEffectInterval();

    public ConfigurationSection getEffectDamage();
}
