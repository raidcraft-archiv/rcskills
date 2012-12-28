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
public class RegisteredTrigger {

    private final Triggered listener;
    private final TriggerExecutor executor;
    private final Skill skill;
    private final boolean ignoreChecks;
    private final boolean cancelEventOnFail;

    public RegisteredTrigger(final Triggered listener, final TriggerExecutor executor, boolean ignoreChecks, boolean cancelEventOnFail) {

        this.listener = listener;
        this.executor = executor;
        this.skill = (listener instanceof Skill ? (Skill) listener : null);
        this.ignoreChecks = ignoreChecks;
        this.cancelEventOnFail = cancelEventOnFail;
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


        Hero hero = trigger.getHero();
        if (skill == null) {
            return;
        }

        if (!skill.isActive() || !skill.isUnlocked() || !hero.equals(skill.getHero())) {
            return;
        }

        if (!ignoreChecks) {
            try {
                // check the skill usage
                skill.checkUsage();
            } catch (CombatException e) {
                hero.sendMessage(ChatColor.RED + e.getMessage());
                // lets check if we need to cancel a bukkit event
                if (cancelEventOnFail && trigger instanceof BukkitEventTrigger) {
                    if (((BukkitEventTrigger) trigger).getEvent() instanceof Cancellable) {
                        ((Cancellable) ((BukkitEventTrigger) trigger).getEvent()).setCancelled(true);
                    }
                }
                return;
            }
        }

        try {
            // add a combat effect when a skill is beeing casted
            if (skill.getProperties().getInformation().triggerCombat()) hero.addEffect(skill, Combat.class);

            // substrat the cost of the skill before it is triggered because a failed trigger costs too
            if (!ignoreChecks) {
                skill.substractUsageCost();
            }

            // and lets pass on the trigger
            executor.execute(listener, trigger);
        } catch (CombatException e) {
            hero.sendMessage(ChatColor.RED + e.getMessage());
        }
    }
}