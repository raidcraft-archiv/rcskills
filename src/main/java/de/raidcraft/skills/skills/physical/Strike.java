package de.raidcraft.skills.skills.physical;

import de.raidcraft.skills.api.TargetedAttack;
import de.raidcraft.skills.api.combat.Callback;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.AbstractLevelableSkill;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.skill.SkillInformation;
import de.raidcraft.skills.tables.THeroSkill;
import org.bukkit.entity.LivingEntity;

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
    public void run(Hero hero, LivingEntity target) throws CombatException {

        hero.damageEntity(target, getTotalDamage(), new Callback() {
            @Override
            public void run(LivingEntity entity) {

                entity.setFireTicks(100);
            }
        });
    }
}
