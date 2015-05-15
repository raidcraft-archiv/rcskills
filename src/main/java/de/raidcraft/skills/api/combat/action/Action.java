package de.raidcraft.skills.api.combat.action;

import de.raidcraft.skills.api.exceptions.CombatException;

/**
 * @author Silthus
 */
public interface Action<T> {

    T getSource();

    void run() throws CombatException;
}
