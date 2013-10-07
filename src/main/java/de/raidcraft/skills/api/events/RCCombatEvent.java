package de.raidcraft.skills.api.events;

import de.raidcraft.skills.api.character.CharacterTemplate;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Silthus
 */
public class RCCombatEvent extends Event {

    public enum Type {

        LEAVE,
        ENTER
    }

    private final CharacterTemplate character;
    private final Type type;

    public RCCombatEvent(CharacterTemplate character, Type type) {

        this.character = character;
        this.type = type;
    }

    public CharacterTemplate getHero() {

        return character;
    }

    public Type getType() {

        return type;
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
