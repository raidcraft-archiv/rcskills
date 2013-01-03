package de.raidcraft.skills.api.persistance;

import de.raidcraft.skills.api.effect.EffectInformation;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public interface EffectData {

    public EffectInformation getInformation();

    public ConfigurationSection getDataMap();

    public double getEffectPriority();

    public int getEffectDuration();

    public int getEffectDelay();

    public int getEffectInterval();

    public int getEffectDamage();

    public double getEffectDamageLevelModifier();

    public double getEffectDamageProfLevelModifier();

    public double getEffectDamageSkillLevelModifier();

    public double getEffectDurationLevelModifier();

    public double getEffectDurationProfLevelModifier();

    public double getEffectDurationSkillLevelModifier();

    public double getEffectDelayLevelModifier();

    public double getEffectDelayProfLevelModifier();

    public double getEffectDelaySkillLevelModifier();

    public double getEffectIntervalLevelModifier();

    public double getEffectIntervalProfLevelModifier();

    public double getEffectIntervalSkillLevelModifier();
}
