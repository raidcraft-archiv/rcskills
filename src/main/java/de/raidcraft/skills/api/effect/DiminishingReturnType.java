package de.raidcraft.skills.api.effect;

import de.raidcraft.util.EnumUtils;

/**
 * @author Silthus
 */
public enum DiminishingReturnType {

    CONTROLLED_ROOT,
    CONTROLLED_STUN,
    DISARM,
    DISORIENT,
    FEAR,
    HORROR,
    OPENER_STUN,
    RANDOM_ROOT,
    RANDOM_STUN,
    SILENCE,
    TAUNT,
    SCATTER,
    NULL;

    public static DiminishingReturnType fromString(String name) {

        return EnumUtils.getEnumFromString(DiminishingReturnType.class, name);
    }
}
