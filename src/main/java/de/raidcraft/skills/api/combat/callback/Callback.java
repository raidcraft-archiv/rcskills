package de.raidcraft.skills.api.combat.callback;

import de.raidcraft.skills.api.exceptions.CombatException;

/**
 * @author Silthus
 */
public interface Callback<T> {

    public void run(T target) throws CombatException;
}
