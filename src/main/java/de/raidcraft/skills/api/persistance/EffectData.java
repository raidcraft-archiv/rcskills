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

    String getFriendlyName();

    String getActivateMessage();

    String getDeactivateMessage();

    String getRenewMessage();

    EffectInformation getInformation();

    boolean isEnabled();

    ConfigurationSection getDataMap();

    int getMaxStacks();

    double getEffectPriority();

    ConfigurationSection getEffectDuration();

    ConfigurationSection getEffectDelay();

    ConfigurationSection getEffectInterval();

    ConfigurationSection getEffectDamage();

    Map<EffectEffectStage, List<AmbientEffect>> getAmbientEffects();
}
