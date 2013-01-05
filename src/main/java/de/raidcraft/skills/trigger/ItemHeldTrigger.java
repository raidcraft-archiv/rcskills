package de.raidcraft.skills.trigger;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.trigger.BukkitEventTrigger;
import de.raidcraft.skills.api.trigger.HandlerList;
import org.bukkit.event.player.PlayerItemHeldEvent;

/**
 * @author Silthus
 */
public class ItemHeldTrigger extends BukkitEventTrigger<Hero, PlayerItemHeldEvent> {


    public ItemHeldTrigger(Hero hero, PlayerItemHeldEvent event) {

        super(hero, event);
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
