package de.raidcraft.skills.api.combat.callback;

import de.raidcraft.skills.api.exceptions.CombatException;

/**
 * @author Silthus
 */
public interface Callback<T> {

    void run(T trigger) throws CombatException;
}
