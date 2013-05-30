package de.raidcraft.skills.trigger;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.trigger.BukkitEventTrigger;
import de.raidcraft.skills.api.trigger.HandlerList;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * @author Silthus
 */
public class PlayerLoginTrigger extends BukkitEventTrigger<PlayerJoinEvent> {

    public PlayerLoginTrigger(CharacterTemplate source, PlayerJoinEvent event) {

        super(source, event);
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
