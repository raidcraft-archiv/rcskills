package de.raidcraft.skills.trigger;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.trigger.BukkitEventTrigger;
import de.raidcraft.skills.api.trigger.HandlerList;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerFishEvent;

/**
 * @author Silthus
 */
public class PlayerFishTrigger extends BukkitEventTrigger<PlayerFishEvent> implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;

    /*///////////////////////////////////////////////////
    //              Needed Trigger Stuff
    ///////////////////////////////////////////////////*/

    public PlayerFishTrigger(CharacterTemplate source, PlayerFishEvent event) {

        super(source, event);
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }

    public HandlerList getHandlers() {

        return handlers;
    }

    @Override
    public boolean isCancelled() {

        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {

        this.cancelled = b;
    }
}
