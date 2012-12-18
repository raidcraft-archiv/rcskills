package de.raidcraft.skills.api.combat.effect;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.persistance.PeriodicEffectData;

/**
 * @author Silthus
 */
public abstract class PeriodicCharacterEffect extends AbstractPeriodicEffect<CharacterTemplate, CharacterTemplate> {

    protected PeriodicCharacterEffect(CharacterTemplate source, CharacterTemplate target, PeriodicEffectData data) {

        super(source, target, data);
    }
}
