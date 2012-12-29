package de.raidcraft.skills.api.combat;

import org.bukkit.event.entity.EntityDamageEvent;

/**
 * @author Silthus
 */
public enum AttackType {

    PHYSICAL,
    MAGICAL,
    UNKNOWN;

    public static AttackType fromEvent(EntityDamageEvent.DamageCause cause) {

        switch (cause) {

            case ENTITY_ATTACK:
            case FALL:
            case CONTACT:
            case SUFFOCATION:
            case BLOCK_EXPLOSION:
            case ENTITY_EXPLOSION:
            case DROWNING:
            case FALLING_BLOCK:
            case POISON:
                return PHYSICAL;
            case FIRE:
            case FIRE_TICK:
            case MAGIC:
            case LIGHTNING:
            case WITHER:
                return MAGICAL;
            default:
                return UNKNOWN;
        }
    }
}
