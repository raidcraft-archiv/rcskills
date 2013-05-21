package de.raidcraft.skills.effects;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.types.ExpirableEffect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.api.skill.Skill;

/**
 * @author Silthus
 */
@EffectInformation(
        name = "Summoned",
        description = "Markiert beschwörte Kreaturen.",
        types = {EffectType.SUMMON, EffectType.MAGICAL}
)
public class Summoned extends ExpirableEffect<Skill> {

    public Summoned(Skill source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {

        // this will make the creature friendly to the party of the summoner
        getSource().getHolder().getParty().addMember(target);
    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {

        target.getEntity().setCustomNameVisible(false);
        target.kill();
    }

    @Override
    protected void renew(CharacterTemplate target) throws CombatException {

    }
}
