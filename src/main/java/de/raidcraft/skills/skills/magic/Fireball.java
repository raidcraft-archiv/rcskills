package de.raidcraft.skills.skills.magic;

import de.raidcraft.skills.api.TargetedAttack;
import de.raidcraft.skills.api.combat.AbstractEffect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.skill.AbstractLevelableSkill;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.skill.SkillInformation;
import de.raidcraft.skills.tables.THeroSkill;
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


    public Fireball(Hero hero, SkillProperties skillData, THeroSkill database) {

        super(hero, skillData, database);
    }

    @Override
    public void run(Hero hero, LivingEntity target) throws CombatException {

        RCFireball fireball = new RCFireball();
        // TODO: set variable strength of the fireball based on the skill level
        fireball.fireTicks = 60 * getLevel().getLevel();
        fireball.incinerate = true;
        fireball.run(target);
        getProfession().getLevel().addExp(2);
        addEffect(new FireballEffect().setDelay(5).setDuration(20).setInterval(2), target);
    }

    public static class FireballEffect extends AbstractEffect {

        @Override
        public void apply(LivingEntity source, LivingEntity target) throws CombatException {

            target.setFireTicks(20);
        }
    }
}
