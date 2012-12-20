package de.raidcraft.skills.api.persistance;

import de.raidcraft.api.config.DataMap;
import de.raidcraft.skills.api.combat.effect.EffectInformation;

/**
 * @author Silthus
 */
public interface EffectData {

    public EffectInformation getInformation();

    public DataMap getDataMap();

    public double getEffectPriority();

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
