package de.raidcraft.skills.api.skill;

import de.raidcraft.skills.api.Level;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillData;

/**
 * @author Silthus
 */
public abstract class AbstractLevelableSkill extends AbstractSkill implements LevelableSkill {

    private final Hero hero;
    private Level<LevelableSkill> level;

    public AbstractLevelableSkill(Hero hero, SkillData skillData) {

        super(skillData);
        this.hero = hero;
    }

    @Override
    public void attachLevel(Level<LevelableSkill> level) {

        this.level = level;
    }

    @Override
    public Hero getHero() {

        return hero;
    }

    @Override
    public Level getLevel() {

        return level;
    }
}
