package de.raidcraft.skills.api.combat;

import org.bukkit.event.entity.EntityDamageEvent;

/**
 * @author Silthus
 */
public enum EffectType {

    BUFF,
    DAMAGING,
    DEBUFF,
    INTERRUPT,
    HARMFUL,
    HELPFUL,
    HEALING,
    MOVEMENT,
    PHYSICAL,
    SILENCABLE,
    SUMMON,
    MAGICAL,
    UNBINDABLE,
    ABSORBING,
    REFLECTING,
    REDUCING,
    IGNORE_ARMOR,
    AREA,
    AURA,
    AVATAR,
    UNKNOWN,
    DEFAULT_ATTACK,
    PROTECTION,
    PURGEABLE;

    public static EffectType fromEvent(EntityDamageEvent.DamageCause cause) {

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
