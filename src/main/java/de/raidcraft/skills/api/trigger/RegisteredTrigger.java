package de.raidcraft.skills.api.trigger;

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
     *
     * @throws org.bukkit.event.EventException
     *          If an event handler throws an exception.
     */
    public abstract void callTrigger(final Trigger trigger) throws EventException;
}
