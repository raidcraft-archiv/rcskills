package de.raidcraft.skills.api.events;

import de.raidcraft.skills.api.character.CharacterTemplate;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Silthus
 */
public class RCMaxHealthChangeEvent extends Event {

    private final CharacterTemplate character;
    private double value;

    public RCMaxHealthChangeEvent(CharacterTemplate character, double value) {

        this.character = character;
        this.value = value;
    }

    public CharacterTemplate getCharacter() {

        return character;
    }

    public double getValue() {

        return value;
    }

    public void setValue(double value) {

        this.value = value;
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
