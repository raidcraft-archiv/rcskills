package de.raidcraft.skills.trigger;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.trigger.HandlerList;
import de.raidcraft.skills.api.trigger.Trigger;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * @author Silthus
 */
public class BlockBreakTrigger extends Trigger {

    private final BlockBreakEvent event;

    public BlockBreakTrigger(Hero hero, BlockBreakEvent event) {

        super(hero);
        this.event = event;
    }

    public BlockBreakEvent getEvent() {

        return event;
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
