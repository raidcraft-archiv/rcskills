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

    private double amount;
    private boolean cancelled = false;
    private final HealAction<?> action;

    public HealTrigger(HealAction action, double amount) {

        super((CharacterTemplate) action.getTarget());
        this.action = action;
        this.amount = amount;
    }

    public CharacterTemplate getTarget() {

        return getSource();
    }

    public HealAction<?> getAction() {

        return action;
    }

    public double getAmount() {

        return amount;
    }

    public void setAmount(double amount) {

        this.amount = amount;
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
