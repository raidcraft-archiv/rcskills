package de.raidcraft.skills.api.combat.action;

import de.raidcraft.skills.api.exceptions.CombatException;

/**
 * @author Silthus
 */
public interface Action<T> {

    public T getSource();

    public void run() throws CombatException;
}
