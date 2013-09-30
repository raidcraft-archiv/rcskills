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

    public int getOverrideInt(String key, int def);

    public double getOverrideDouble(String key, double def);

    public String getOverrideString(String key, String def);

    public boolean getOverrideBool(String key, boolean def);

    public ConfigurationSection getOverrideSection(String path);

    public DataMap getOverrideConfig();

    public void setOverrideConfig(DataMap override);

    public ConfigurationSection getSafeConfigSection(String path);

    boolean isEnabled();

    void setEnabled(boolean enabled);

    boolean isLevelable();

    boolean canUseInCombat();

    boolean canUseOutOfCombat();

    public Map<AbilityEffectStage, List<AmbientEffect>> getAmbientEffects();

    public Set<EffectType> getTypes();

    public Set<EffectElement> getElements();

    ConfigurationSection getData();

    T getInformation();

    ConfigurationSection getDamage();

    ConfigurationSection getCastTime();

    ConfigurationSection getRange();

    ConfigurationSection getCooldown();
}
