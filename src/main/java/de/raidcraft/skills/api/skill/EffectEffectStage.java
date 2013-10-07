package de.raidcraft.skills.api.skill;

import de.raidcraft.util.EnumUtils;

/**
 * @author Silthus
 */
public enum EffectEffectStage {

    APPLY,
    REMOVE,
    RENEW,
    TICK,
    DAMAGE;

    public static EffectEffectStage fromString(String name) {

        return EnumUtils.getEnumFromString(EffectEffectStage.class, name);
    }
}
