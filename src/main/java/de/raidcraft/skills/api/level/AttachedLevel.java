package de.raidcraft.skills.api.level;

import de.raidcraft.skills.api.level.forumla.LevelFormula;

/**
 * @author Silthus
 */
public interface AttachedLevel<T extends Levelable> {

    public T getLevelObject();

    public int getLevel();

    public int getMaxLevel();

    public int getExp();

    /**
     * Gets the level formula containing the information
     * how much exp is needed to the next level.
     *
     * @return formula to calculate exp
     */
    public LevelFormula getFormula();

    /**
     * Gets the maximum amount of exp for this level.
     *
     * @return max exp for level
     */
    public int getMaxExp();

    /**
     * Calculates the total amount of exp needed to that level from level 0 and 0 exp.
     *
     * @param level to calculate exp for
     * @return total exp
     */
    public int getTotalNeededExpForLevel(int level);

    /**
     * Calculates and sets the maxp exp for the current level.
     */
    public void calculateMaxExp();

    /**
     * Gets the amount of exp needed for the next level.
     *
     * @return exp needed for the next level
     *         minus the one you already have
     */
    public int getExpToNextLevel();

    public void addExp(int exp);

    public void addExp(int exp, boolean callEvent);

    public void removeExp(int exp);

    public void removeExp(int exp, boolean callEvent);

    public void setExp(int exp);

    public void setExp(int exp, boolean callEvent);

    public void setLevel(int level);

    public void addLevel(int level);

    public void removeLevel(int level);

    /**
     * Checks if the player can level.
     * That means checking the current exp vs the needed exp for this level.
     *
     * @return true if player can level
     */
    public boolean canLevel();

    public boolean hasReachedMaxLevel();

    public void saveLevelProgress();
}
