package de.raidcraft.skills.trigger;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.resource.Resource;
import de.raidcraft.skills.api.trigger.HandlerList;
import de.raidcraft.skills.api.trigger.Trigger;
import org.bukkit.event.Cancellable;

/**
 * @author Silthus
 */
public class ResourceChangeTrigger extends Trigger implements Cancellable {

    private final Resource resource;
    private int newValue = 0;
    private boolean cancelled = false;

    public ResourceChangeTrigger(Hero source, Resource resource, int newValue) {

        super(source);
        this.resource = resource;
        this.newValue = newValue;
    }

    public Hero getHero() {

        return (Hero) getSource();
    }

    public Resource getResource() {

        return resource;
    }

    public int getNewValue() {

        return newValue;
    }

    public void setNewValue(int newValue) {

        this.newValue = newValue;
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
    public void setCancelled(boolean cancelled) {

        this.cancelled = cancelled;
    }
}
