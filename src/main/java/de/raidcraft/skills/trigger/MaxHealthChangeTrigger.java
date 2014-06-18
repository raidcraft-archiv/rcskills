package de.raidcraft.skills.trigger;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.events.RCMaxHealthChangeEvent;
import de.raidcraft.skills.api.trigger.BukkitEventTrigger;
import de.raidcraft.skills.api.trigger.HandlerList;

/**
 * @author Silthus
 */
public class MaxHealthChangeTrigger extends BukkitEventTrigger<RCMaxHealthChangeEvent> {


    private static final HandlerList handlers = new HandlerList();

    /*///////////////////////////////////////////////////
    //              Needed Trigger Stuff
    ///////////////////////////////////////////////////*/

    public MaxHealthChangeTrigger(CharacterTemplate source, RCMaxHealthChangeEvent event) {

        super(source, event);
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }

    public HandlerList getHandlers() {

        return handlers;
    }
}
