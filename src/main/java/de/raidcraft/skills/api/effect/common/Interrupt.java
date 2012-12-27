package de.raidcraft.skills.api.effect.common;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.effect.AbstractEffect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;

/**
 * @author Silthus
 */
public class Interrupt<S> extends AbstractEffect<S> {

    public Interrupt(S source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {

        // interrupt all spells that are currently casted
        target.removeEffect(CastTime.class);
        // and remove ourself directly after
        remove();
    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {
    }

    @Override
    protected void renew(CharacterTemplate target) throws CombatException {
    }
}
