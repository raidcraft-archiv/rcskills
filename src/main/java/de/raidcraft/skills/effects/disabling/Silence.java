package de.raidcraft.skills.effects.disabling;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.effect.DiminishingReturnType;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.types.ExpirableEffect;
import de.raidcraft.skills.api.effect.common.CastTime;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;

/**
 * @author Silthus
 */
@EffectInformation(
        name = "Silence",
        description = "Lässt den Gegener verstummen.",
        types = {EffectType.DEBUFF, EffectType.HARMFUL, EffectType.MAGICAL},
        diminishingReturn = DiminishingReturnType.SILENCE
)
public class Silence<S> extends ExpirableEffect<S> {

    public Silence(S source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {

        warn("Du hast einen Stille Effekt erhalten und kannst keine Zauber wirken!");
        // also cancel casts
        target.removeEffect(CastTime.class);
    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {

        warn("Der Stille Effekt wurde aufgehoben und du kannst wieder Zauber wirken!");
    }

    @Override
    protected void renew(CharacterTemplate target) throws CombatException {

    }
}
