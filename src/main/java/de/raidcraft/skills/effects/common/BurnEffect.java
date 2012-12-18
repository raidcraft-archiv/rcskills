package de.raidcraft.skills.effects.common;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.effect.AbstractTimedEffect;
import de.raidcraft.skills.api.combat.effect.EffectInformation;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.PeriodicEffectData;

/**
 * @author Silthus
 */
@EffectInformation(
        name = "BurnEffect",
        description = "Verbrennt das Ziel",
        types = {}
)
public class BurnEffect extends AbstractTimedEffect<CharacterTemplate, CharacterTemplate> {

    public BurnEffect(CharacterTemplate source, CharacterTemplate target, PeriodicEffectData data) {

        super(source, target, data);
    }

    @Override
    public void apply(CharacterTemplate target) throws CombatException {

        target.getEntity().setFireTicks(getDuration());
    }
}
