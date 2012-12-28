package de.raidcraft.skills.trigger;

import de.raidcraft.skills.api.combat.action.Attack;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.trigger.HandlerList;
import de.raidcraft.skills.api.trigger.Trigger;

/**
 * @author Silthus
 */
public class DamageTrigger extends Trigger {

    private final Attack attack;

    public DamageTrigger(Hero hero, Attack attack) {

        super(hero);
        this.attack = attack;
    }

    public Attack getAttack() {

        return attack;
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
