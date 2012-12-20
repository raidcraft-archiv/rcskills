package de.raidcraft.skills.skills.physical;

import de.raidcraft.api.InvalidTargetException;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.attack.PhysicalAttack;
import de.raidcraft.skills.api.combat.callback.Callback;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.AbstractLevelableSkill;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.skill.SkillInformation;
import de.raidcraft.skills.api.skill.type.TargetedAttack;
import de.raidcraft.skills.effects.common.KnockBackEffect;
import de.raidcraft.skills.tables.THeroSkill;

/**
 * @author Silthus
 */
@SkillInformation(
        name = "Strike",
        desc = "Schlägt einen Gegner und setzt ihn in Brand.",
        types = {Skill.Type.DAMAGING}
)
public class Strike extends AbstractLevelableSkill implements TargetedAttack {

    public Strike(Hero hero, SkillProperties data, Profession profession,  THeroSkill database) {

        super(hero, data, profession, database);
    }

    @Override
    public void run(Hero hero, CharacterTemplate target) throws CombatException, InvalidTargetException {

        new PhysicalAttack(hero, target, getTotalDamage(), new Callback() {
            @Override
            public void run(CharacterTemplate target) throws CombatException {

                target.addEffect(Strike.this, KnockBackEffect.class);
            }
        }).run();
    }
}
