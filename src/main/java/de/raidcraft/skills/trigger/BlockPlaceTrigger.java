package de.raidcraft.skills.trigger;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.trigger.BukkitEventTrigger;
import de.raidcraft.skills.api.trigger.HandlerList;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * @author Silthus
 */
public class BlockPlaceTrigger extends BukkitEventTrigger<BlockPlaceEvent> {


    private static final HandlerList handlers = new HandlerList();

    /*///////////////////////////////////////////////////
    //              Needed Trigger Stuff
    ///////////////////////////////////////////////////*/

    public BlockPlaceTrigger(Hero hero, BlockPlaceEvent event) {

        super(hero, event);
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }

    public HandlerList getHandlers() {

        return handlers;
    }
}
