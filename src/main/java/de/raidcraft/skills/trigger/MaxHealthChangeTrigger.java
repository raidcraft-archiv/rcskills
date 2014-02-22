package de.raidcraft.skills.trigger;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.events.RCMaxHealthChangeEvent;
import de.raidcraft.skills.api.trigger.BukkitEventTrigger;
import de.raidcraft.skills.api.trigger.HandlerList;

/**
 * @author Silthus
 */
public class MaxHealthChangeTrigger extends BukkitEventTrigger<RCMaxHealthChangeEvent> {


    public MaxHealthChangeTrigger(CharacterTemplate source, RCMaxHealthChangeEvent event) {

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