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

    private static final HandlerList handlers = new HandlerList();
    private final Resource resource;
    private double newValue = 0;
    private boolean cancelled = false;

    public ResourceChangeTrigger(Hero source, Resource resource, double newValue) {

        super(source);
        this.resource = resource;
        this.newValue = newValue;
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }

    public Hero getHero() {

        return (Hero) getSource();
    }

    public Action getAction() {

        if (getNewValue() == getResource().getCurrent()) {
            return Action.NO_CHANGE;
        } else if (getNewValue() > getResource().getCurrent()) {
            return Action.GAIN;
        }
        return Action.LOSS;
    }

    public double getNewValue() {

        return newValue;
    }

    public Resource getResource() {

        return resource;
    }

    /*///////////////////////////////////////////////////
    //              Needed Trigger Stuff
    ///////////////////////////////////////////////////*/

    public void setNewValue(double newValue) {

        this.newValue = newValue;
    }

    public HandlerList getHandlers() {

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

    public enum Action {

        GAIN,
        LOSS,
        NO_CHANGE
    }
}
