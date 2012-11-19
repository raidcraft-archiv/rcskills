package de.raidcraft.skills.api;

import de.raidcraft.skills.api.exceptions.CombatException;

/**
 * @author Silthus
 */
public interface Active<T> {

    public void run(T trigger) throws CombatException;
}
