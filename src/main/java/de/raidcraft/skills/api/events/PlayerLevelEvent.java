package de.raidcraft.skills.api.events;

import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.skills.SkilledPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Silthus
 */
public class PlayerLevelEvent extends Event implements Cancellable {

    private final SkilledPlayer player;
    private final int nextLevel;
    private boolean cancelled = false;

    public PlayerLevelEvent(SkilledPlayer player, int nextLevel) {

        this.player = player;
        this.nextLevel = nextLevel;
    }

    public SkilledPlayer getSkilledPlayer() {

        return player;
    }

    public RCPlayer getPlayer() {

        return player.getPlayer();
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
