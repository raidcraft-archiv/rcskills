package de.raidcraft.skills.api.combat.action;

import org.bukkit.event.Cancellable;

/**
 * @author Silthus
 */
public interface TargetedAction<S, T> extends Action<S>, Cancellable {

    public T getTarget();

    public int getThreat();

    public void combatLog(Object o, String message);
}
