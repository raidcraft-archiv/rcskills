package de.raidcraft.skills.skills;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.types.ExpirableEffect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;

/**
 * @author Silthus
 */
@EffectInformation(
        name = "timed-permission",
        description = "Represents a generic timed permissions effect."
)
public class TimedPermissionSkill extends ExpirableEffect<PermissionSkill> {

    public TimedPermissionSkill(PermissionSkill source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {


    }

    @Override
    protected void renew(CharacterTemplate target) throws CombatException {


    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {

        getSource().remove();
    }
}
