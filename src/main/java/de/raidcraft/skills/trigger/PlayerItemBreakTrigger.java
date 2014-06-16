package de.raidcraft.skills.trigger;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.trigger.BukkitEventTrigger;
import de.raidcraft.skills.api.trigger.HandlerList;
import org.bukkit.event.player.PlayerItemBreakEvent;

/**
 * @author Silthus
 */
public class PlayerItemBreakTrigger extends BukkitEventTrigger<PlayerItemBreakEvent> {

    private static final HandlerList handlers = new HandlerList();

    /*///////////////////////////////////////////////////
    //              Needed Trigger Stuff
    ///////////////////////////////////////////////////*/

    public PlayerItemBreakTrigger(CharacterTemplate source, PlayerItemBreakEvent event) {

        super(source, event);
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }

    public HandlerList getHandlers() {

        return handlers;
    }
}
