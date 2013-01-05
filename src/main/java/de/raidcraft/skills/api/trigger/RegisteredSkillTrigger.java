package de.raidcraft.skills.api.trigger;

import de.raidcraft.skills.api.effect.common.Combat;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.ChatColor;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventException;

/**
 * Stores relevant information for plugin listeners
 */
public class RegisteredSkillTrigger extends RegisteredTrigger {

    private final Skill skill;
    private final TriggerHandler info;

    public RegisteredSkillTrigger(final Triggered listener, final TriggerExecutor executor, TriggerHandler info) {

        super(listener, executor);
        this.skill = (listener instanceof Skill ? (Skill) listener : null);
        this.info = info;
    }

    /**
     * Calls the event executor
     *
     * @param trigger The event
     *
     * @throws EventException If an event handler throws an exception.
     */
    public void callTrigger(final Trigger trigger) throws EventException {


        if (skill == null) {
            return;
        }

        if (!skill.isActive() || !skill.isUnlocked() || !trigger.getSource().equals(skill.getHero())) {
            return;
        }

        try {
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
        } catch (CombatException e) {
            if (trigger.getSource() instanceof Hero) {
                ((Hero) trigger.getSource()).sendMessage(ChatColor.RED + e.getMessage());
            }
            // lets check if we need to cancel a bukkit event
            if (info.cancelEventOnFail() && trigger instanceof BukkitEventTrigger) {
                if (((BukkitEventTrigger) trigger).getEvent() instanceof Cancellable) {
                    ((Cancellable) ((BukkitEventTrigger) trigger).getEvent()).setCancelled(true);
                }
            }
        }
    }
}