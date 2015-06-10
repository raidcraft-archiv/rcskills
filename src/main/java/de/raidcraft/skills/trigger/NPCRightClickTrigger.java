package de.raidcraft.skills.trigger;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.trigger.BukkitEventTrigger;
import de.raidcraft.skills.api.trigger.HandlerList;
import net.citizensnpcs.api.event.NPCRightClickEvent;

/**
 * @author Silthus
 */
public class NPCRightClickTrigger extends BukkitEventTrigger<NPCRightClickEvent> {

    private static final HandlerList handlers = new HandlerList();

    /*///////////////////////////////////////////////////
    //              Needed Trigger Stuff
    ///////////////////////////////////////////////////*/

    public NPCRightClickTrigger(Hero hero, NPCRightClickEvent event) {

        super(hero, event);
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }

    public HandlerList getHandlers() {

        return handlers;
    }
}
