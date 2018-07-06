package de.raidcraft.skills.api.traits;

import de.raidcraft.skills.api.character.CharacterTemplate;
import lombok.Data;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author mreichenbach
 */
@Data
public class AddCharacterTraitEvent extends Event {

    private final CharacterTemplate character;
    private final CharacterTrait trait;

    //<-- Handler -->//
    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {

        return handlers;
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }
}
