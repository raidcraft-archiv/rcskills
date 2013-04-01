package de.raidcraft.skills.api.events;

import de.raidcraft.skills.api.level.AttachedLevel;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Silthus
 */
public class RCExpGainEvent extends Event implements Cancellable {

    private final AttachedLevel attachedLevel;
    private int gainedExp;
    private boolean cancelled = false;

    public RCExpGainEvent(AttachedLevel attachedLevel, int gainedExp) {

        this.attachedLevel = attachedLevel;
        this.gainedExp = gainedExp;
    }

    public AttachedLevel getAttachedLevel() {

        return attachedLevel;
    }

    public int getGainedExp() {

        return gainedExp;
    }

    public void setGainedExp(int gainedExp) {

        this.gainedExp = gainedExp;
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

    @Override
    public boolean isCancelled() {

        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {

        this.cancelled = cancel;
    }
}
