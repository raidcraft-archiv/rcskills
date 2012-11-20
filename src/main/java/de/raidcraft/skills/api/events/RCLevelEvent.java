package de.raidcraft.skills.api.events;

import de.raidcraft.skills.api.Level;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Silthus
 */
public class RCLevelEvent extends Event implements Cancellable {

    private final int nextLevel;
    private boolean cancelled = false;

    public RCLevelEvent(Level level, int nextLevel) {

        this.nextLevel = nextLevel;
    }

    public int getNextLevel() {

        return nextLevel;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }

    /*///////////////////////////////////////////////////
    //              Needed Bukkit Stuff
    ///////////////////////////////////////////////////*/

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
