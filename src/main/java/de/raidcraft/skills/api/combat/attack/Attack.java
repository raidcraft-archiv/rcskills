package de.raidcraft.skills.api.combat.attack;

import de.raidcraft.api.InvalidTargetException;
import de.raidcraft.skills.api.exceptions.CombatException;

/**
 * @author Silthus
 */
public interface Attack<A, T> {

    public A getAttacker();

    public T getTarget();

    public int getDamage();

    public void run() throws CombatException, InvalidTargetException;
}
