package de.raidcraft.skills.trigger;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.trigger.BukkitEventTrigger;
import de.raidcraft.skills.api.trigger.HandlerList;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

/**
 * @author Silthus
 */
public class EntityTargetTrigger extends BukkitEventTrigger<EntityTargetLivingEntityEvent> {

    public EntityTargetTrigger(CharacterTemplate hero, EntityTargetLivingEntityEvent event) {

        super(hero, event);
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
