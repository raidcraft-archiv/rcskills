package de.raidcraft.skills.api.skill;

import de.raidcraft.api.database.Database;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.Level;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.tables.THeroSkill;

/**
 * @author Silthus
 */
public abstract class AbstractLevelableSkill extends AbstractSkill implements LevelableSkill {

    private final Level<LevelableSkill> level;

    public AbstractLevelableSkill(Hero hero, SkillProperties data, Profession profession,  THeroSkill database) {

        super(hero, data, profession, database);
        this.level = new SkillLevel(this, database);
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

        int stamina = (int) (super.getTotalStaminaCost() + (getProperties().getSkillLevelStaminaCostModifier() * getLevel().getLevel()));
        if (stamina > 20) stamina = 20;
        return stamina;
    }

    @Override
    public final int getTotalHealthCost() {

        return (int) (super.getTotalHealthCost() + (getProperties().getSkillLevelHealthCostModifier() * getLevel().getLevel()));
    }

    @Override
    public final int getTotalCastTime() {

        return (int) (super.getTotalCastTime() + (getProperties().getSkillLevelCastTimeModifier() * getLevel().getLevel()));
    }

    @Override
    public final boolean isMastered() {

        return getLevel().getMaxLevel() == getLevel().getLevel();
    }

    @Override
    public void onExpGain(int exp) {

        // override if needed
    }

    @Override
    public void onExpLoss(int exp) {

        // override if needed
    }

    @Override
    public void onLevelGain(int level) {

        // override if needed
    }

    @Override
    public void onLevelLoss(int level) {

        // override if needed
    }

    @Override
    public void save() {

        super.save();
        level.saveLevelProgress();
    }

    @Override
    public final void saveLevelProgress(Level<LevelableSkill> level) {

        database.setLevel(level.getLevel());
        database.setExp(level.getExp());
        Database.save(database);
    }

    @Override
    public final boolean equals(Object obj) {

        return obj instanceof LevelableSkill
                && super.equals(obj);
    }
}
