package de.raidcraft.skills.api.ability;

import de.raidcraft.skills.api.exceptions.CombatException;

/**
 * @author Silthus
 */
public interface Useable {

    void use() throws CombatException;
}
