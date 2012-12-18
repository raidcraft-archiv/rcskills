package de.raidcraft.skills.api.persistance;

/**
 * @author Silthus
 */
public interface PeriodicEffectData extends EffectData {

    public int getEffectDuration();

    public int getEffectDelay();

    public int getEffectInterval();

    public double getEffectDurationLevelModifier();

    public double getEffectDurationProfLevelModifier();

    public double getEffectDelayLevelModifier();

    public double getEffectDelayProfLevelModifier();

    public double getEffectIntervalLevelModifier();

    public double getEffectIntervalProfLevelModifier();
}
