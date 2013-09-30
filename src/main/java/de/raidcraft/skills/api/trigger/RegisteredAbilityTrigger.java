package de.raidcraft.skills.api.trigger;

import de.raidcraft.skills.api.ability.Ability;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.event.EventException;

/**
 * Stores relevant information for plugin listeners
 */
public class RegisteredAbilityTrigger extends RegisteredTrigger {

    private final Ability ability;

    public RegisteredAbilityTrigger(final Triggered listener, final TriggerExecutor executor, TriggerHandler info) {

        super(listener, executor, info);
        this.ability = (listener instanceof Ability ? (Ability) listener : null);
    }

    /**
     * Calls the event executor
     *
     * @param trigger The event
     *
     * @throws EventException If an event handler throws an exception.
     */
    protected void call(final Trigger trigger) throws EventException, CombatException {


        if (ability == null) {
            return;
        }

        if (ability instanceof Skill) {
            if (!((Skill) ability).isActive() || !((Skill) ability).isUnlocked()) {
                return;
            }
        }

        if (info.filterTargets() && !trigger.getSource().equals(ability.getHolder())) {
            return;
        }

        // also abort if the skill is combat only or non combat
        if (!ability.getProperties().canUseOutOfCombat() && !trigger.getSource().isInCombat()) {
            return;
        }
        if (!ability.getProperties().canUseInCombat() && trigger.getSource().isInCombat()) {
            return;
        }
        // and lets pass on the trigger
        executor.execute(listener, trigger);
    }
}