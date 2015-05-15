package de.raidcraft.skills.api.combat.action;

import org.bukkit.event.Cancellable;

/**
 * @author Silthus
 */
public interface TargetedAction<S, T> extends Action<S>, Cancellable {

    T getTarget();

    void setTarget(T target);

    double getThreat();

    void combatLog(Object o, String message);
}
