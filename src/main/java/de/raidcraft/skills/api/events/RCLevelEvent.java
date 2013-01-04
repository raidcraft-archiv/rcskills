package de.raidcraft.skills.api.events;

import de.raidcraft.skills.api.level.Level;
import de.raidcraft.skills.api.level.Levelable;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Silthus
 */
public class RCLevelEvent<S extends Levelable<S>> extends Event implements Cancellable {

    private final S source;
    private final int oldLevel;
    private final int newLevel;
    private boolean cancelled = false;

    public RCLevelEvent(S source, int oldLevel, int newLevel) {

        this.source = source;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
    }

    public S getSource() {

        return source;
    }

    public Level<S> getLevel() {

        return source.getLevel();
    }

    public int getOldLevel() {

        return oldLevel;
    }

    public int getNewLevel() {

        return newLevel;
    }

    @Override
    public boolean isCancelled() {

        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {

        this.cancelled = cancelled;
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
