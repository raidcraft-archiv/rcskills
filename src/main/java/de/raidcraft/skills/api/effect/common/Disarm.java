package de.raidcraft.skills.api.effect.common;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.effect.ExpirableEffect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;

/**
 * @author Silthus
 */
public class Disarm<S> extends ExpirableEffect<S> {


    public Disarm(S source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {

        warn("Du wurdest entwaffnet und kannst nicht angreifen!");
    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {

        warn("Du bist nicht mehr entwaffnet und kannst wieder angreifen.");
    }

    @Override
    protected void renew(CharacterTemplate target) throws CombatException {

    }
}
