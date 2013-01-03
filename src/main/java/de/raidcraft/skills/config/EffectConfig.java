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
    public double getEffectPriority() {

        return getOverride("priority", getPlugin().getCommonConfig().default_effect_priority);
    }

    @Override
    public int getEffectDuration() {

        return getOverride("duration.base", 0);
    }

    @Override
    public int getEffectDelay() {

        return getOverride("delay.base", 0);
    }

    @Override
    public int getEffectInterval() {

        return getOverride("interval.base", 0);
    }

    @Override
    public double getEffectDurationLevelModifier() {

        return getOverride("duration.level-modifier", 0.0);
    }

    @Override
    public double getEffectDurationProfLevelModifier() {

        return getOverride("duration.prof-level-modifier", 0.0);
    }

    @Override
    public double getEffectDelayLevelModifier() {

        return getOverride("delay.level-modifier", 0.0);
    }

    @Override
    public double getEffectDelayProfLevelModifier() {

        return getOverride("delay.prof-level-modifier", 0.0);
    }

    @Override
    public double getEffectIntervalLevelModifier() {

        return getOverride("interval.level-modifier", 0.0);
    }

    @Override
    public double getEffectIntervalProfLevelModifier() {

        return getOverride("interval.prof-level-modifier", 0.0);
    }

    @Override
    public int getEffectDamage() {

        return getOverride("damage.base", 0);
    }

    @Override
    public double getEffectDamageLevelModifier() {

        return getOverride("damage.level-modifier", 0.0);
    }

    @Override
    public double getEffectDamageProfLevelModifier() {

        return getOverride("damage.prof-level-modifier", 0.0);
    }

    @Override
    public double getEffectDamageSkillLevelModifier() {

        return getOverride("damage.skill-level-modifier", 0.0);
    }

    @Override
    public double getEffectDurationSkillLevelModifier() {

        return getOverride("duration.skill-level-modifier", 0.0);
    }

    @Override
    public double getEffectDelaySkillLevelModifier() {

        return getOverride("delay.skill-level-modifier", 0.0);
    }

    @Override
    public double getEffectIntervalSkillLevelModifier() {

        return getOverride("interval.skill-level-modifier", 0.0);
    }
}
