package de.raidcraft.skills.trigger;

import de.raidcraft.skills.api.combat.action.Attack;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.trigger.HandlerList;
import de.raidcraft.skills.api.trigger.Trigger;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * @author Silthus
 */
public class DamageTrigger extends Trigger {

    private final Attack attack;
    private final EntityDamageEvent.DamageCause cause;

    public DamageTrigger(Hero hero, Attack attack, EntityDamageEvent.DamageCause cause) {

        super(hero);
        this.attack = attack;
        this.cause = cause;
    }

    public Attack getAttack() {

        return attack;
    }

    public EntityDamageEvent.DamageCause getCause() {

        return cause;
    }

    /*///////////////////////////////////////////////////
    //              Needed Trigger Stuff
    ///////////////////////////////////////////////////*/

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
