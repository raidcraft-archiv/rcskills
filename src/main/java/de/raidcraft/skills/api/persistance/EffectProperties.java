package de.raidcraft.skills.api.persistance;

/**
 * @author Silthus
 */
public interface EffectProperties {

    public double getEffectPriority();

    public int getEffectDuration();

    public int getEffectDelay();

    public int getEffectInterval();

    public double getEffectDurationLevelModifier();

    public double getEffectDurationSkillLevelModifier();

    public double getEffectDurationProfLevelModifier();

    public double getEffectDelayLevelModifier();

    public double getEffectDelaySkillLevelModifier();

    public double getEffectDelayProfLevelModifier();

    public double getEffectIntervalLevelModifier();

    public double getEffectIntervalSkillLevelModifier();

    public double getEffectIntervalProfLevelModifier();
}
