package de.raidcraft.skills.api.events;

import de.raidcraft.skills.api.character.CharacterTemplate;
import lombok.Data;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Silthus
 */
@Data
public class RCEntityDeathEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final CharacterTemplate character;
    private boolean cancelled = false;

    public RCEntityDeathEvent(CharacterTemplate character) {

        this.character = character;
    }

    /*///////////////////////////////////////////////////
    //              Needed Bukkit Stuff
    ///////////////////////////////////////////////////*/

    public static HandlerList getHandlerList() {

        return handlers;
    }

    public HandlerList getHandlers() {

        return handlers;
    }
}
