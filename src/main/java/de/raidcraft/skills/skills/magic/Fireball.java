package de.raidcraft.skills.skills.magic;

import de.raidcraft.skills.api.TargetedAttack;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.LevelData;
import de.raidcraft.skills.api.persistance.SkillData;
import de.raidcraft.skills.api.skill.AbstractLevelableSkill;
import de.raidcraft.skills.api.skill.SkillInformation;
import de.raidcraft.skills.api.skill.SkillType;
import de.raidcraft.spells.fire.RCFireball;
import org.bukkit.entity.LivingEntity;

/**
 * @author Silthus
 */
@SkillInformation(
        name = "fireball",
        desc = "Schießt einen Feuerball auf den Gegener.",
        types = {SkillType.DAMAGING, SkillType.FIRE, SkillType.HARMFUL}
)
public class Fireball extends AbstractLevelableSkill implements TargetedAttack {


    public Fireball(Hero hero, SkillData skillData, LevelData levelData) {

        super(hero, skillData, levelData);
    }

    @Override
    public void run(Hero hero, LivingEntity target) throws CombatException {

            RCFireball fireball = new RCFireball();
            // TODO: set variable strength of the fireball based on the skill level
            fireball.fireTicks = 60;
            fireball.incinerate = true;
            fireball.run(target);
    }
}
