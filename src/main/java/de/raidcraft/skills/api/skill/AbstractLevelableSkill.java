package de.raidcraft.skills.api.skill;

import com.avaje.ebean.Ebean;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.Level;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.tables.THeroSkill;

/**
 * @author Silthus
 */
public abstract class AbstractLevelableSkill extends AbstractSkill implements LevelableSkill {

    private Level<LevelableSkill> level;

    public AbstractLevelableSkill(Hero hero, SkillProperties data, THeroSkill database) {

        super(hero, data, database);
        attachLevel(new SkillLevel(this, database));
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
    public int getMaxLevel() {

        return getProperties().getMaxLevel();
    }

    @Override
    public int getTotalDamage() {

        return (int) (super.getTotalDamage() + (getProperties().getSkillLevelDamageModifier() * getLevel().getLevel()));
    }

    @Override
    public int getTotalManaCost() {

        return (int) (super.getTotalManaCost() + (getProperties().getSkillLevelManaCostModifier() * getLevel().getLevel()));
    }

    @Override
    public int getTotalStaminaCost() {

        return (int) (super.getTotalStaminaCost() + (getProperties().getSkillLevelStaminaCostModifier() * getLevel().getLevel()));
    }

    @Override
    public int getTotalHealthCost() {

        return (int) (super.getTotalHealthCost() + (getProperties().getSkillLevelHealthCostModifier() * getLevel().getLevel()));
    }

    @Override
    public boolean isMastered() {

        return getLevel().getMaxLevel() == getLevel().getLevel();
    }

    @Override
    public boolean isUnlocked() {

        return super.isUnlocked() && getLevel().getLevel() > 0;
    }

    @Override
    public void decreaseLevel(Level<LevelableSkill> level) {

        // override if needed
    }

    @Override
    public void increaseLevel(Level<LevelableSkill> level) {

        // override if needed
    }

    @Override
    public final void saveLevelProgress(Level<LevelableSkill> level) {

        THeroSkill skill = Ebean.find(THeroSkill.class, getId());
        skill.setLevel(level.getLevel());
        skill.setExp(level.getExp());
        Ebean.save(skill);
    }

    @Override
    public boolean equals(Object obj) {

        return obj instanceof LevelableSkill
                && super.equals(obj);
    }
}
