package de.raidcraft.skills.config;

import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.skills.EffectFactory;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.persistance.EffectData;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;

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
}
