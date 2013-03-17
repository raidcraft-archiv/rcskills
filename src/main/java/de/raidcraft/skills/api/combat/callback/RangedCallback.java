package de.raidcraft.skills.api.combat.callback;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.exceptions.CombatException;

/**
 * @author Silthus
 */
public interface RangedCallback extends ProjectileCallback<CharacterTemplate> {

    @Override
    void run(CharacterTemplate target) throws CombatException;
}
