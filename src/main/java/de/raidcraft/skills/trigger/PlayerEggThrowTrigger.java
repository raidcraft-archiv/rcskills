package de.raidcraft.skills.trigger;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.trigger.BukkitEventTrigger;
import de.raidcraft.skills.api.trigger.HandlerList;
import org.bukkit.event.player.PlayerEggThrowEvent;

/**
 * @author Silthus
 */
public class PlayerEggThrowTrigger extends BukkitEventTrigger<PlayerEggThrowEvent> {

    private static final HandlerList handlers = new HandlerList();

 /*///////////////////////////////////////////////////
    //              Needed Trigger Stuff
    ///////////////////////////////////////////////////*/

    public PlayerEggThrowTrigger(CharacterTemplate source, PlayerEggThrowEvent event) {

        super(source, event);
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }

    public HandlerList getHandlers() {

        return handlers;
    }
}
