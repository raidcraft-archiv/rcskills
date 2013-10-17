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

    public enum Action {

        GAIN,
        LOSS,
        NO_CHANGE;
    }

    private final Resource resource;
    private double newValue = 0;
    private boolean cancelled = false;

    public ResourceChangeTrigger(Hero source, Resource resource, double newValue) {

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

    public double getNewValue() {

        return newValue;
    }

    public void setNewValue(double newValue) {

        this.newValue = newValue;
    }

    public Action getAction() {

        if (getNewValue() == getResource().getCurrent()) {
            return Action.NO_CHANGE;
        } else if (getNewValue() > getResource().getCurrent()) {
            return Action.GAIN;
        }
        return Action.LOSS;
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
