package de.raidcraft.skills.api.combat;

import de.raidcraft.util.EnumUtils;

/**
 * @author Silthus
 */
public enum EffectElement {

    FIRE,
    WATER,
    ICE,
    EARTH,
    DARK,
    LIGHTNING,
    HOLY,
    AIR;

    public static EffectElement fromString(String name) {

        return EnumUtils.getEnumFromString(EffectElement.class, name);
    }
}
