package de.raidcraft.skills.api.persistance;

import de.raidcraft.api.ambient.AmbientEffect;
import de.raidcraft.skills.api.skill.AbilityEffectStage;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Map;

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

    public Map<AbilityEffectStage, List<AmbientEffect>> getAmbientEffects();

    ConfigurationSection getData();

    T getInformation();

    ConfigurationSection getDamage();

    ConfigurationSection getCastTime();

    ConfigurationSection getRange();

    ConfigurationSection getCooldown();
}
