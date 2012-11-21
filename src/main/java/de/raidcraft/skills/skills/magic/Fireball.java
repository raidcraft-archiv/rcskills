package de.raidcraft.skills.skills.magic;

import de.raidcraft.skills.api.level.Level;
import de.raidcraft.skills.api.level.Levelable;
import de.raidcraft.skills.api.TargetedAttack;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.LevelData;
import de.raidcraft.skills.api.persistance.SkillData;
import de.raidcraft.skills.api.skill.*;
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

        super(hero, skillData);
    }

    @Override
    public SkillResult run(Hero hero, LivingEntity target) throws CombatException {

        RCFireball fireball = new RCFireball();
        // TODO: set variable strength of the fireball based on the skill level
        fireball.fireTicks = 60 * getLevel().getLevel();
        fireball.incinerate = true;
        fireball.run(target);
        if (getProfession() instanceof Levelable) {
            ((Levelable) getProfession()).getLevel().addExp(2);
        }
        return SkillResult.NORMAL;
    }

    @Override
    public void increaseLevel(Level<LevelableSkill> level) {
        //TODO: implement
    }

    @Override
    public void decreaseLevel(Level<LevelableSkill> level) {
        //TODO: implement
    }

    @Override
    public void saveLevelProgress(Level<LevelableSkill> level) {
        //TODO: implement
    }
}
