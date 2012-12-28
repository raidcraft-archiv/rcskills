package de.raidcraft.skills.api.trigger;

import de.raidcraft.skills.api.effect.common.Combat;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.ChatColor;
import org.bukkit.event.EventException;

/**
 * Stores relevant information for plugin listeners
 */
public class RegisteredTrigger {

    private final Triggered listener;
    private final TriggerExecutor executor;
    private final Skill skill;
    private final boolean ignoreChecks;

    public RegisteredTrigger(final Triggered listener, final TriggerExecutor executor, boolean ignoreChecks) {

        this.listener = listener;
        this.executor = executor;
        this.skill = (listener instanceof Skill ? (Skill) listener : null);
        this.ignoreChecks = ignoreChecks;
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

        if (!skill.isActive() || !skill.isUnlocked() && !hero.equals(skill.getHero())) {
            return;
        }

        if (ignoreChecks) {
            try {
                // check the skill usage
                skill.checkUsage();
                // add a combat effect when a skill is beeing casted
                if (skill.getProperties().getInformation().triggerCombat()) hero.addEffect(skill, Combat.class);
            } catch (CombatException e) {
                hero.sendMessage(ChatColor.RED + e.getMessage());
            }

            // substract the mana, health and stamina cost
            if (skill.getTotalManaCost() > 0) hero.setMana(hero.getMana() - skill.getTotalManaCost());
            if (skill.getTotalStaminaCost() > 0) hero.setStamina(hero.getStamina() - skill.getTotalStaminaCost());
            if (skill.getTotalHealthCost() > 0) hero.damage(skill.getTotalHealthCost());
            // keep this last or items will be removed before casting
            hero.getPlayer().getInventory().removeItem(skill.getProperties().getReagents());
        }

        try {
            // and lets pass on the trigger
            executor.execute(listener, trigger);
        } catch (Exception e) {
            hero.sendMessage(ChatColor.RED + e.getMessage());
        }
    }
}