package de.raidcraft.skills.api.trigger;

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

    public boolean isIgnoringCancelled() {

        return info.ignoreCancelled();
    }

    /**
     * Calls the event executor
     *
     * @param trigger The event
     */
    public void callTrigger(final Trigger trigger) throws EventException {

        if (trigger instanceof Cancellable && !isIgnoringCancelled()) {
            return;
        }
        call(trigger);
    }

    protected abstract void call(final Trigger trigger) throws EventException;
}
