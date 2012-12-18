package de.raidcraft.skills.effects.common;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.effect.AbstractPeriodicEffect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.PeriodicEffectData;

/**
 * @author Silthus
 */
public class BurnEffect extends AbstractPeriodicEffect<CharacterTemplate, CharacterTemplate> {


    public BurnEffect(CharacterTemplate source, CharacterTemplate target, PeriodicEffectData data) {

        super(source, target, data);
    }

    @Override
    public void apply(CharacterTemplate target) throws CombatException {
        //TODO: implement
    }
}
