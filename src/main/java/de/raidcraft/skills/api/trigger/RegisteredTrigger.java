package de.raidcraft.skills.api.trigger;

import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.event.EventException;

/**
 * Stores relevant information for plugin listeners
 */
public class RegisteredTrigger {

    private final Triggered listener;
    private final TriggerExecutor executor;
    private final Skill skill;

    public RegisteredTrigger(final Triggered listener, final TriggerExecutor executor) {

        this.listener = listener;
        this.executor = executor;
        this.skill = (listener instanceof Skill ? (Skill) listener : null);
    }

    /**
     * Gets the listener for this registration
     *
     * @return Registered Listener
     */
    public Triggered getListener() {

        return listener;
    }

    /**
     * Calls the event executor
     *
     * @param trigger The event
     *
     * @throws EventException If an event handler throws an exception.
     */
    public void callTrigger(final Trigger trigger) throws EventException {

        if (skill != null) {
            if (!skill.isActive() || !skill.isUnlocked() && !trigger.getHero().equals(skill.getHero())) {
                return;
            }
        }
        executor.execute(listener, trigger);
    }
}