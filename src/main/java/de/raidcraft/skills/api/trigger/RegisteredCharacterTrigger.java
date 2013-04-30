package de.raidcraft.skills.api.trigger;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.exceptions.CombatException;
import org.bukkit.event.EventException;

/**
 * Stores relevant information for plugin listeners
 */
public class RegisteredCharacterTrigger extends RegisteredTrigger {

    private final CharacterTemplate character;

    public RegisteredCharacterTrigger(final Triggered listener, final TriggerExecutor executor, TriggerHandler info) {

        super(listener, executor, info);
        this.character = (listener instanceof CharacterTemplate ? (CharacterTemplate) listener : null);
    }

    /**
     * Calls the event executor
     *
     * @param trigger The event
     *
     * @throws org.bukkit.event.EventException If an event handler throws an exception.
     */
    protected void call(final Trigger trigger) throws EventException, CombatException {


        if (character == null) {
            return;
        }

        if (!trigger.getSource().equals(character)) {
            return;
        }

        // and lets pass on the trigger
        executor.execute(listener, trigger);
    }
}