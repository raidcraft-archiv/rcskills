package de.raidcraft.skills.api.skill;

import de.raidcraft.util.EnumUtils;

/**
 * @author Silthus
 */
public enum AbilityEffectStage {

    CASTING,
    CAST,
    LINE,
    IMPACT,
    HIT,
    BOW_FIRE;

    public static AbilityEffectStage fromString(String name) {

        return EnumUtils.getEnumFromString(AbilityEffectStage.class, name);
    }
}
