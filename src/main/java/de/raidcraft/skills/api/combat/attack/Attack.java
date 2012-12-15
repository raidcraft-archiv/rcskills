package de.raidcraft.skills.api.combat.attack;

/**
 * @author Silthus
 */
public interface Attack<A, T> extends Runnable {

    public A getAttacker();

    public T getTarget();

    public int getDamage();
}
