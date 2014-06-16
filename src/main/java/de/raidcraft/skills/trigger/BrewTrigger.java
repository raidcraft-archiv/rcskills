package de.raidcraft.skills.trigger;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.trigger.BukkitEventTrigger;
import de.raidcraft.skills.api.trigger.HandlerList;
import org.bukkit.event.inventory.BrewEvent;

/**
 * @author Silthus
 */
public class BrewTrigger extends BukkitEventTrigger<BrewEvent> {

    private static final HandlerList handlers = new HandlerList();

    /*///////////////////////////////////////////////////
    //              Needed Trigger Stuff
    ///////////////////////////////////////////////////*/

    public BrewTrigger(Hero hero, BrewEvent event) {

        super(hero, event);
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }

    public HandlerList getHandlers() {

        return handlers;
    }
}
