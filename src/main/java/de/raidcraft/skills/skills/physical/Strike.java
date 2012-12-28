package de.raidcraft.skills.skills.physical;

import de.raidcraft.api.InvalidTargetException;
import de.raidcraft.skills.api.EffectType;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.attack.PhysicalAttack;
import de.raidcraft.skills.api.combat.callback.Callback;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.AbstractLevelableSkill;
import de.raidcraft.skills.api.skill.SkillInformation;
import de.raidcraft.skills.effects.disabling.Stun;
import de.raidcraft.skills.tables.THeroSkill;

/**
 * @author Silthus
 */
@SkillInformation(
        name = "Strike",
        desc = "Schlägt einen Gegner und setzt ihn in Brand.",
        types = {EffectType.DAMAGING, EffectType.PHYSICAL},
        triggerCombat = true
)
public class Strike extends AbstractLevelableSkill {

    public Strike(Hero hero, SkillProperties data, Profession profession,  THeroSkill database) {

        super(hero, data, profession, database);
    }

    public void run(final Hero hero, CharacterTemplate target) throws CombatException, InvalidTargetException {

        new PhysicalAttack(hero, target, getTotalDamage(), new Callback() {
            @Override
            public void run(CharacterTemplate target) throws CombatException {

                target.addEffect(Strike.this, hero, Stun.class);
            }
        }).run();
    }

    @Override
    public void apply() {
        //TODO: implement
    }

    @Override
    public void remove() {
        //TODO: implement
    }
}
