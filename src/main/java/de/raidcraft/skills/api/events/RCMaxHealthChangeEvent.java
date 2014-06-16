package de.raidcraft.skills.api.events;

import de.raidcraft.skills.api.character.CharacterTemplate;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Silthus
 */
public class RCMaxHealthChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final CharacterTemplate character;
    private double value;

    public RCMaxHealthChangeEvent(CharacterTemplate character, double value) {

        this.character = character;
        this.value = value;
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }

    public CharacterTemplate getCharacter() {

        return character;
    }

    /*///////////////////////////////////////////////////
    //              Needed Bukkit Stuff
    ///////////////////////////////////////////////////*/

    public double getValue() {

        return value;
    }

    public void setValue(double value) {

        this.value = value;
    }

    public HandlerList getHandlers() {

        return handlers;
    }
}
