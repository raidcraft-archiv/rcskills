package de.raidcraft.skills.api.combat.action;

import org.bukkit.event.Cancellable;

/**
 * @author Silthus
 */
public interface Attack<S, T> extends Action<S>, Cancellable {

    public T getTarget();

    public int getDamage();

    public void setDamage(int damage);
}
