package de.raidcraft.skills.api.persistance;

import de.raidcraft.api.ambient.AmbientEffect;
import de.raidcraft.api.config.DataMap;
import de.raidcraft.skills.api.combat.EffectElement;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.skill.AbilityEffectStage;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Silthus
 */
public interface AbilityProperties<T> extends ConfigurationSection {

    String getName();

    String getFriendlyName();

    String getDescription();

    String[] getUsage();

    int getOverrideInt(String key, int def);

    double getOverrideDouble(String key, double def);

    String getOverrideString(String key, String def);

    boolean getOverrideBool(String key, boolean def);

    ConfigurationSection getOverrideSection(String path);

    DataMap getOverrideConfig();

    void setOverrideConfig(DataMap override);

    ConfigurationSection getSafeConfigSection(String path);

    boolean isEnabled();

    void setEnabled(boolean enabled);

    boolean isLevelable();

    boolean canUseInCombat();

    boolean canUseOutOfCombat();

    Map<AbilityEffectStage, List<AmbientEffect>> getAmbientEffects();

    Set<EffectType> getTypes();

    Set<EffectElement> getElements();

    ConfigurationSection getData();

    T getInformation();

    ConfigurationSection getDamage();

    ConfigurationSection getCastTime();

    ConfigurationSection getRange();

    ConfigurationSection getCooldown();
}
