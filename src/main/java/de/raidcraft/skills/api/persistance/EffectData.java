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

    public long getEffectDuration();

    public long getEffectDelay();

    public long getEffectInterval();

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
