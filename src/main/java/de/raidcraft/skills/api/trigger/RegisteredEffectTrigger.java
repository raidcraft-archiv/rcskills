package de.raidcraft.skills.api.trigger;

import de.raidcraft.skills.api.effect.Effect;
import de.raidcraft.skills.api.exceptions.CombatException;
import org.bukkit.event.EventException;

/**
 * Stores relevant information for plugin listeners
 */
public class RegisteredEffectTrigger extends RegisteredTrigger {

    private final Effect effect;

    public RegisteredEffectTrigger(final Triggered listener, final TriggerExecutor executor, TriggerHandler info) {

        super(listener, executor, info);
        this.effect = (listener instanceof Effect ? (Effect) listener : null);
    }

    /**
     * Calls the event executor
     *
     * @param trigger The event
     *
     * @throws org.bukkit.event.EventException If an event handler throws an exception.
     */
    protected void call(final Trigger trigger) throws CombatException, EventException {


        if (effect == null) {
            return;
        }

        if (info.filterTargets() && !effect.getTarget().equals(trigger.getSource())) {
            return;
        }
        // and lets pass on the trigger
        executor.execute(listener, trigger);
    }
}