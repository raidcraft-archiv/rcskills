package de.raidcraft.skills.api.events;

import de.raidcraft.skills.api.level.AttachedLevel;
import de.raidcraft.skills.api.level.Levelable;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Silthus
 */
public class RCLevelEvent<S extends Levelable<S>> extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final S source;
    private final int oldLevel;
    private final int newLevel;
    private boolean cancelled = false;

    public RCLevelEvent(S source, int oldLevel, int newLevel) {

        this.source = source;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }

    public S getSource() {

        return source;
    }

    public AttachedLevel<S> getLevel() {

        return source.getAttachedLevel();
    }

    public int getOldLevel() {

        return oldLevel;
    }

    public int getNewLevel() {

        return newLevel;
    }

        /*///////////////////////////////////////////////////
    //              Needed Bukkit Stuff
    ///////////////////////////////////////////////////*/

    @Override
    public boolean isCancelled() {

        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {

        this.cancelled = cancelled;
    }

    public HandlerList getHandlers() {

        return handlers;
    }
}
