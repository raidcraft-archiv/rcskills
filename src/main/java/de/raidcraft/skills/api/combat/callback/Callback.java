package de.raidcraft.skills.api.combat.callback;

import de.raidcraft.api.InvalidTargetException;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.exceptions.CombatException;

/**
 * @author Silthus
 */
public interface Callback {

    public void run(CharacterTemplate target) throws CombatException, InvalidTargetException;
}