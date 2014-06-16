package de.raidcraft.skills.api.trigger;

import de.raidcraft.skills.api.exceptions.CombatException;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventException;

/**
 * @author Silthus
 */
public abstract class RegisteredTrigger {

    protected final Triggered listener;
    protected final TriggerExecutor executor;
    protected final TriggerHandler info;

    public RegisteredTrigger(final Triggered listener, final TriggerExecutor executor, TriggerHandler info) {

        this.listener = listener;
        this.executor = executor;
        this.info = info;
    }

    /**
     * Gets the listener for this registration
     *
     * @return Registered Listener
     */
    public Triggered getListener() {

        return listener;
    }

    public TriggerPriority getPriority() {

        return info.priority();
    }

    /**
     * Calls the event executor
     *
     * @param trigger The event
     */
    public void callTrigger(final Trigger trigger) throws CombatException, EventException {

        if (trigger instanceof Cancellable && !isIgnoringCancelled() && ((Cancellable) trigger).isCancelled()) {
            return;
        }
        call(trigger);
    }

    public boolean isIgnoringCancelled() {

        return info.ignoreCancelled();
    }

    protected abstract void call(final Trigger trigger) throws CombatException, EventException;
}
