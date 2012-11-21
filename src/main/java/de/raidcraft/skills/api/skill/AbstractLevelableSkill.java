package de.raidcraft.skills.api.skill;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.Level;
import de.raidcraft.skills.api.persistance.SkillData;

/**
 * @author Silthus
 */
public abstract class AbstractLevelableSkill extends AbstractSkill implements LevelableSkill {

    private Level<LevelableSkill> level;

    public AbstractLevelableSkill(Hero hero, SkillData data) {

        super(hero, data);
    }

    @Override
    public void attachLevel(Level<LevelableSkill> level) {

        this.level = level;
    }

    @Override
    public Level<LevelableSkill> getLevel() {

        return level;
    }

    @Override
    public boolean equals(Object obj) {

        return obj instanceof LevelableSkill
                && super.equals(obj)
                && ((LevelableSkill) obj).getHero().getName().equalsIgnoreCase(getHero().getName());
    }
}
