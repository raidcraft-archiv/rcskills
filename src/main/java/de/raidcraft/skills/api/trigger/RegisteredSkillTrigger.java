package de.raidcraft.skills.api.trigger;

import de.raidcraft.skills.api.effect.common.Combat;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.event.EventException;

/**
 * Stores relevant information for plugin listeners
 */
public class RegisteredSkillTrigger extends RegisteredTrigger {

    private final Skill skill;

    public RegisteredSkillTrigger(final Triggered listener, final TriggerExecutor executor, TriggerHandler info) {

        super(listener, executor, info);
        this.skill = (listener instanceof Skill ? (Skill) listener : null);
    }

    /**
     * Calls the event executor
     *
     * @param trigger The event
     *
     * @throws EventException If an event handler throws an exception.
     */
    protected void call(final Trigger trigger) throws EventException, CombatException {


        if (skill == null) {
            return;
        }

        if (!skill.isActive() || !skill.isUnlocked() || !trigger.getSource().equals(skill.getHero())) {
            return;
        }

        if (info.checkUsage()) {
            // check the skill usage
            skill.checkUsage();
        }

        // add a combat effect when a skill is beeing casted
        if (skill.getProperties().getInformation().triggerCombat()) trigger.getSource().addEffect(skill, Combat.class);

        // and lets pass on the trigger
        executor.execute(listener, trigger);

        // only substract usage cost if the skill does not fail when executing
        if (info.substractUsageCosts()) {
            skill.substractUsageCost();
        }
    }
}