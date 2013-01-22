package de.raidcraft.skills.trigger;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.trigger.BukkitEventTrigger;
import de.raidcraft.skills.api.trigger.HandlerList;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityShootBowEvent;

/**
 * @author Silthus
 */
public class BowFireTrigger extends BukkitEventTrigger<EntityShootBowEvent> implements Cancellable {

    private boolean cancelled = false;

    public BowFireTrigger(CharacterTemplate source, EntityShootBowEvent event) {

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

    @Override
    public boolean isCancelled() {

        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {

        this.cancelled = b;
    }
}
