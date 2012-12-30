package de.raidcraft.skills.api.trigger;

import de.raidcraft.skills.api.effect.Effect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import org.bukkit.ChatColor;
import org.bukkit.event.EventException;

/**
 * Stores relevant information for plugin listeners
 */
public class RegisteredEffectTrigger extends RegisteredTrigger {

    private final Effect effect;

    public RegisteredEffectTrigger(final Triggered listener, final TriggerExecutor executor) {

        super(listener, executor);
        this.effect = (listener instanceof Effect ? (Effect) listener : null);
    }

    /**
     * Calls the event executor
     *
     * @param trigger The event
     *
     * @throws org.bukkit.event.EventException If an event handler throws an exception.
     */
    public void callTrigger(final Trigger trigger) throws EventException {


        Hero hero = trigger.getHero();
        if (effect == null) {
            return;
        }

        if (!(effect.getTarget() instanceof Hero) || !effect.getTarget().equals(hero)) {
            return;
        }

        try {
            // and lets pass on the trigger
            executor.execute(listener, trigger);
        } catch (CombatException e) {
            ((Hero) effect.getTarget()).sendMessage(ChatColor.RED + e.getMessage());
        }
    }
}