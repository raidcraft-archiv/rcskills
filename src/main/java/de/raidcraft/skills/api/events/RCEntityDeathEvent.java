package de.raidcraft.skills.api.events;

import de.raidcraft.skills.api.character.CharacterTemplate;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Silthus
 */
public class RCEntityDeathEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final CharacterTemplate character;

    public RCEntityDeathEvent(CharacterTemplate character) {

        this.character = character;
    }

    /*///////////////////////////////////////////////////
    //              Needed Bukkit Stuff
    ///////////////////////////////////////////////////*/

    public static HandlerList getHandlerList() {

        return handlers;
    }

    public CharacterTemplate getCharacter() {

        return character;
    }

    public HandlerList getHandlers() {

        return handlers;
    }
}
