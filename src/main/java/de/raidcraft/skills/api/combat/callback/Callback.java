package de.raidcraft.skills.api.combat.callback;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.exceptions.CombatException;

/**
 * @author Silthus
 */
public interface Callback {

    public void run(CharacterTemplate entity) throws CombatException;
}
