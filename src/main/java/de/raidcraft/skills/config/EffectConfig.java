package de.raidcraft.skills.config;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.ambient.AmbientEffect;
import de.raidcraft.api.ambient.AmbientManager;
import de.raidcraft.api.ambient.UnknownAmbientEffect;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.skills.EffectFactory;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.api.skill.EffectEffectStage;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Silthus
 */
public class EffectConfig extends ConfigurationBase<SkillsPlugin> implements EffectData {

    private final EffectFactory factory;

    public EffectConfig(EffectFactory factory) {

        super(factory.getPlugin(), new File(
                new File(factory.getPlugin().getDataFolder(), factory.getPlugin().getCommonConfig().effect_config_path),
                factory.getEffectName() + ".yml"));
        this.factory = factory;
    }

    @Override
    public String getName() {

        return factory.getEffectName();
    }

    public String getFriendlyName() {

        return getOverride("name", factory.getEffectName());
    }

    @Override
    public String getActivateMessage() {

        return getOverrideString("activate-message", null);
    }

    @Override
    public String getDeactivateMessage() {

        return getOverrideString("deactivate-message", null);
    }

    @Override
    public String getRenewMessage() {

        return getOverrideString("renew-message", null);
    }

    @Override
    public ConfigurationSection getDataMap() {

        return getOverrideSection("custom");
    }

    @Override
    public EffectInformation getInformation() {

        return factory.getInformation();
    }

    @Override
    public boolean isEnabled() {

        return getBoolean("enabled", true);
    }

    @Override
    public int getMaxStacks() {

        return getOverrideInt("max-stacks", 5);
    }

    @Override
    public double getEffectPriority() {

        return getOverride("priority", getPlugin().getCommonConfig().default_effect_priority);
    }

    @Override
    public ConfigurationSection getEffectDuration() {

        return getOverrideSection("duration");
    }

    @Override
    public ConfigurationSection getEffectDelay() {

        return getOverrideSection("delay");
    }

    @Override
    public ConfigurationSection getEffectInterval() {

        return getOverrideSection("interval");
    }

    @Override
    public ConfigurationSection getEffectDamage() {

        return getOverrideSection("damage");
    }

    @Override
    public Map<EffectEffectStage, List<AmbientEffect>> getAmbientEffects() {

        HashMap<EffectEffectStage, List<AmbientEffect>> effects = new HashMap<>();
        ConfigurationSection root = getOverrideSection("visual-effects");
        for (EffectEffectStage stage : EffectEffectStage.values()) {
            ConfigurationSection section = root.getConfigurationSection(stage.name().toLowerCase());
            if (section == null) {
                continue;
            }
            if (!effects.containsKey(stage)) {
                effects.put(stage, new ArrayList<AmbientEffect>());
            }
            Set<String> keys = section.getKeys(false);
            for (String key : keys) {
                try {
                    effects.get(stage).add(AmbientManager.getEffect(section.getConfigurationSection(key)));
                } catch (UnknownAmbientEffect e) {
                    RaidCraft.LOGGER.warning(e.getMessage());
                }
            }
        }
        return effects;
    }
}
