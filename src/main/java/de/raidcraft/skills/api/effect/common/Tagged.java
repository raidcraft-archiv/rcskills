package de.raidcraft.skills.api.effect.common;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.types.ExpirableEffect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;

/**
 * @author Silthus
 */
@EffectInformation(
        name = "Tagged",
        description = "Taggs the target and gives exp to the group that tagged it on death.",
        priority = 1.0,
        global = true
)
public class Tagged extends ExpirableEffect<CharacterTemplate> {

    public Tagged(CharacterTemplate source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {
        //TODO: implement
    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {
        //TODO: implement
    }

    @Override
    protected void renew(CharacterTemplate target) throws CombatException {
        //TODO: implement
    }
}
