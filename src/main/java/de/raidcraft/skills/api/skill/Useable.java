package de.raidcraft.skills.api.skill;

import de.raidcraft.skills.api.exceptions.CombatException;

/**
 * @author Silthus
 */
public interface Useable {

    public void use() throws CombatException;
}
