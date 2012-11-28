package de.raidcraft.skills.api.combat;

import de.raidcraft.skills.api.exceptions.CombatException;
import org.bukkit.entity.LivingEntity;

/**
 * An effect is something that can be applied to entities around in the world.
 * An effect can also be scheduled to apply periodically or after a set time.
 *
 * @author Silthus
 */
public interface Effect {

    public void apply(LivingEntity source, LivingEntity target) throws CombatException;

    public Effect setDuration(int duration);

    public int getDuration();

    public Effect setDelay(int delay);

    public int getDelay();

    public Effect setInterval(int interval);

    public int getInterval();
}
