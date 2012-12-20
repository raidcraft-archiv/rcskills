package de.raidcraft.skills.api.combat.effect;

/**
 * @author Silthus
 */
public interface TimedEffect<S, T> extends Effect<S, T> {

    public long getDuration();
}
