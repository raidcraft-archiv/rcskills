package de.raidcraft.skills.api.combat.effect;

/**
 * @author Silthus
 */
public interface PeriodicEffect<S, T> extends Effect<S, T>, Runnable {

    public int getTaskId();

    public int getDuration();

    public int getDelay();

    public int getInterval();
}
