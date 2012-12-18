package de.raidcraft.skills.api.persistance;

import de.raidcraft.skills.api.combat.effect.EffectInformation;
import de.raidcraft.util.DataMap;

/**
 * @author Silthus
 */
public interface EffectData {

    public EffectInformation getInformation();

    public double getEffectPriority();

    public DataMap getDataMap();
}
