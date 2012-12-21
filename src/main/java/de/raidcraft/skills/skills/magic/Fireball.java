package de.raidcraft.skills.skills.magic;

import de.raidcraft.api.InvalidTargetException;
import de.raidcraft.skills.api.EffectElement;
import de.raidcraft.skills.api.EffectType;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.ProjectileType;
import de.raidcraft.skills.api.combat.attack.MagicalAttack;
import de.raidcraft.skills.api.combat.attack.RangedAttack;
import de.raidcraft.skills.api.combat.callback.RangedCallback;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.AbstractLevelableSkill;
import de.raidcraft.skills.api.skill.SkillInformation;
import de.raidcraft.skills.api.skill.type.AreaAttack;
import de.raidcraft.skills.effects.common.BurnEffect;
import de.raidcraft.skills.tables.THeroSkill;
import org.bukkit.Location;

/**
 * @author Silthus
 */
@SkillInformation(
        name = "fireball",
        desc = "Schießt einen Feuerball auf den Gegener.",
        types = {EffectType.DAMAGING, EffectType.DEBUFF, EffectType.HARMFUL},
        elements = {EffectElement.FIRE}
)
public class Fireball extends AbstractLevelableSkill implements AreaAttack {

    public Fireball(Hero hero, SkillProperties skillData, Profession profession, THeroSkill database) {

        super(hero, skillData, profession, database);
    }

    @Override
    public void run(final Hero hero, final Location target) throws CombatException, InvalidTargetException {

        new RangedAttack(hero, ProjectileType.FIREBALL, new RangedCallback() {
            @Override
            public void run(CharacterTemplate target) throws CombatException, InvalidTargetException {

                new MagicalAttack(hero, target, getTotalDamage()).run();
                target.addEffect(Fireball.this, BurnEffect.class);
                // add some exp to the profession and skill
                getProfession().getLevel().addExp(2);
                getLevel().addExp(5);
            }
        }).run();
    }
}
