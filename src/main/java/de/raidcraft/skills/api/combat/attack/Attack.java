package de.raidcraft.skills.api.combat.attack;

/**
 * @author Silthus
 */
public interface Attack<S, T> extends Action<S> {

    public T getTarget();

    public int getDamage();

    public void setDamage(int damage);
}
