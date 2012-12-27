package de.raidcraft.skills.trigger;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.trigger.HandlerList;
import de.raidcraft.skills.api.trigger.Trigger;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * @author Silthus
 */
public class BlockPlaceTrigger extends Trigger {

    private final BlockPlaceEvent event;

    public BlockPlaceTrigger(Hero hero, BlockPlaceEvent event) {

        super(hero);
        this.event = event;
    }

    public BlockPlaceEvent getEvent() {

        return event;
    }

    @Override
    protected String getTriggerName() {

        return "BlockPlaceTrigger";
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
