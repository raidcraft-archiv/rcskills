package de.raidcraft.skills.api.combat.effect;

import de.raidcraft.skills.api.exceptions.CombatException;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author Silthus
 */
public interface ScheduledEffect<S> extends Effect<S>, Runnable {

    public BukkitTask getTask();

    public boolean isStarted();

    public void startTask();

    public void stopTask();

    public void remove() throws CombatException;

    public void renew() throws CombatException;
}
