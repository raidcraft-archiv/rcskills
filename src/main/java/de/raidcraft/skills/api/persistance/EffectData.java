package de.raidcraft.skills.api.persistance;

import de.raidcraft.api.ambient.AmbientEffect;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.skill.EffectEffectStage;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Map;

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

    public Map<EffectEffectStage, List<AmbientEffect>> getAmbientEffects();
}
