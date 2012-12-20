package de.raidcraft.skills.api.combat.effect;

/**
 * @author Silthus
 */
public interface PeriodicEffect<S, T> extends TimedEffect<S, T>, Runnable {

    public int getTaskId();

    public long getDelay();

    public long getInterval();
}
