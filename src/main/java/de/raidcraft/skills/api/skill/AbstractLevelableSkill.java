package de.raidcraft.skills.api.skill;

import com.avaje.ebean.Ebean;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.Level;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.tables.THeroSkill;

/**
 * @author Silthus
 */
public abstract class AbstractLevelableSkill extends AbstractSkill implements LevelableSkill {

    private Level<LevelableSkill> level;

    public AbstractLevelableSkill(Hero hero, SkillProperties data, THeroSkill database, Profession profession) {

        super(hero, data, database, profession);
        attachLevel(new SkillLevel(this, database));
    }

    @Override
    public final void attachLevel(Level<LevelableSkill> level) {

        this.level = level;
    }

    @Override
    public final Level<LevelableSkill> getLevel() {

        return level;
    }

    @Override
    public final int getMaxLevel() {

        return getProperties().getMaxLevel();
    }

    @Override
    public final int getTotalDamage() {

        return (int) (super.getTotalDamage() + (getProperties().getSkillLevelDamageModifier() * getLevel().getLevel()));
    }

    @Override
    public final int getTotalManaCost() {

        return (int) (super.getTotalManaCost() + (getProperties().getSkillLevelManaCostModifier() * getLevel().getLevel()));
    }

    @Override
    public final int getTotalStaminaCost() {

        return (int) (super.getTotalStaminaCost() + (getProperties().getSkillLevelStaminaCostModifier() * getLevel().getLevel()));
    }

    @Override
    public final int getTotalHealthCost() {

        return (int) (super.getTotalHealthCost() + (getProperties().getSkillLevelHealthCostModifier() * getLevel().getLevel()));
    }

    @Override
    public final boolean isMastered() {

        return getLevel().getMaxLevel() == getLevel().getLevel();
    }

    @Override
    public final boolean isUnlocked() {

        return super.isUnlocked() && getLevel().getLevel() > 0;
    }

    @Override
    public void onLevelDown(Level<LevelableSkill> level) {

        // override if needed
    }

    @Override
    public void onLevelUp(Level<LevelableSkill> level) {

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
    public final boolean equals(Object obj) {

        return obj instanceof LevelableSkill
                && super.equals(obj);
    }
}
