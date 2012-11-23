package de.raidcraft.skills.skills.magic;

import de.raidcraft.skills.api.TargetedAttack;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillData;
import de.raidcraft.skills.api.skill.AbstractLevelableSkill;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.skill.SkillInformation;
import de.raidcraft.spells.fire.RCFireball;
import org.bukkit.entity.LivingEntity;

/**
 * @author Silthus
 */
@SkillInformation(
        name = "fireball",
        desc = "Schießt einen Feuerball auf den Gegener.",
        types = {Skill.Type.DAMAGING, Skill.Type.FIRE, Skill.Type.HARMFUL}
)
public class Fireball extends AbstractLevelableSkill implements TargetedAttack {


    public Fireball(Hero hero, SkillData skillData) {

        super(hero, skillData);
    }

    @Override
    public Result run(Hero hero, LivingEntity target) throws CombatException {

        RCFireball fireball = new RCFireball();
        // TODO: set variable strength of the fireball based on the skill level
        fireball.fireTicks = 60 * getLevel().getLevel();
        fireball.incinerate = true;
        fireball.run(target);
        getProfession().getLevel().addExp(2);
        return Result.NORMAL;
    }
}
