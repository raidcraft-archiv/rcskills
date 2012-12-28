package de.raidcraft.skills.api.trigger;

import de.raidcraft.skills.api.effect.common.Combat;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.util.TimeUtil;
import org.bukkit.ChatColor;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventException;

/**
 * Stores relevant information for plugin listeners
 */
public class RegisteredSkillTrigger extends RegisteredTrigger {

    private final Skill skill;
    private final boolean ignoreChecks;
    private final boolean cancelEventOnFail;

    public RegisteredSkillTrigger(final Triggered listener, final TriggerExecutor executor, boolean ignoreChecks, boolean cancelEventOnFail) {

        super(listener, executor);
        this.skill = (listener instanceof Skill ? (Skill) listener : null);
        this.ignoreChecks = ignoreChecks;
        this.cancelEventOnFail = cancelEventOnFail;
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

        try {
            if (!ignoreChecks) {
                    // check the skill usage
                    skill.checkUsage();
            }

            // add a combat effect when a skill is beeing casted
            if (skill.getProperties().getInformation().triggerCombat()) hero.addEffect(skill, Combat.class);

            // substrat the cost of the skill before it is triggered because a failed trigger costs too
            if (!ignoreChecks) {
                skill.substractUsageCost();
            }

            // and lets pass on the trigger
            executor.execute(listener, trigger);
        } catch (CombatException e) {
            String msg = e.getMessage();
            if (e.getType() == CombatException.Type.ON_COOLDOWN) {
                msg = e.getMessage() + " - " + TimeUtil.millisToSeconds(skill.getRemainingCooldown()) + "s";
            }
            // send the combat warning
            hero.sendMessage(ChatColor.RED + msg);
            // lets check if we need to cancel a bukkit event
            if (cancelEventOnFail && trigger instanceof BukkitEventTrigger) {
                if (((BukkitEventTrigger) trigger).getEvent() instanceof Cancellable) {
                    ((Cancellable) ((BukkitEventTrigger) trigger).getEvent()).setCancelled(true);
                }
            }
        }
    }
}