package de.raidcraft.skills.api.persistance;

/**
 * @author Silthus
 */
public interface EffectProperties {

    public int getDuration();

    public int getDelay();

    public int getInterval();

    public double getDurationLevelModifier();

    public double getDurationSkillLevelModifier();

    public double getDurationProfLevelModifier();

    public double getDelayLevelModifier();

    public double getDelaySkillLevelModifier();

    public double getDelayProfLevelModifier();

    public double getIntervalLevelModifier();

    public double getIntervalSkillLevelModifier();

    public double getIntervalProfLevelModifier();
}
