package de.raidcraft.skills.api.persistance;

/**
 * @author Silthus
 */
public abstract class PlayerSkillLevelData {

    protected int level;
    protected int maxLevel;
    protected int exp;
    protected int maxExp;

    public int getLevel() {

        return level;
    }

    public int getMaxLevel() {

        return maxLevel;
    }

    public int getExp() {

        return exp;
    }

    public int getMaxExp() {

        return maxExp;
    }
}
