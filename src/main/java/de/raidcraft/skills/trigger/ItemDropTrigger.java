package de.raidcraft.skills.trigger;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.trigger.BukkitEventTrigger;
import de.raidcraft.skills.api.trigger.HandlerList;
import org.bukkit.event.player.PlayerDropItemEvent;

/**
 * @author mdoering
 */
public class ItemDropTrigger extends BukkitEventTrigger<PlayerDropItemEvent> {

    /*///////////////////////////////////////////////////
    //              Needed Trigger Stuff
    ///////////////////////////////////////////////////*/

    private static final HandlerList handlers = new HandlerList();

    public ItemDropTrigger(Hero hero, PlayerDropItemEvent event) {

        super(hero, event);
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }

    public HandlerList getHandlers() {

        return handlers;
    }
}
