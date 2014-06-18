package de.raidcraft.skills.trigger;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.action.HealAction;
import de.raidcraft.skills.api.trigger.HandlerList;
import de.raidcraft.skills.api.trigger.Trigger;
import org.bukkit.event.Cancellable;

/**
 * @author Silthus
 */
public class HealTrigger extends Trigger implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final HealAction<?> action;
    private double amount;
    private boolean cancelled = false;

    public HealTrigger(HealAction action, double amount) {

        super((CharacterTemplate) action.getTarget());
        this.action = action;
        this.amount = amount;
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }

    public CharacterTemplate getTarget() {

        return getSource();
    }

    public HealAction<?> getAction() {

        return action;
    }

    /*///////////////////////////////////////////////////
    //              Needed Trigger Stuff
    ///////////////////////////////////////////////////*/

    public double getAmount() {

        return amount;
    }

    public void setAmount(double amount) {

        this.amount = amount;
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
}
