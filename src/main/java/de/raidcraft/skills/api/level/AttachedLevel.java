package de.raidcraft.skills.api.level;

import de.raidcraft.skills.api.level.forumla.LevelFormula;

/**
 * @author Silthus
 */
public interface AttachedLevel<T extends Levelable> {

    T getLevelObject();

    int getLevel();

    void setLevel(int level);

    int getMaxLevel();

    int getExp();

    void setExp(int exp);

    /**
     * Gets the level formula containing the information
     * how much exp is needed to the next level.
     *
     * @return formula to calculate exp
     */
    LevelFormula getFormula();

    /**
     * Gets the maximum amount of exp for this level.
     *
     * @return max exp for level
     */
    int getMaxExp();

    /**
     * Calculates the total amount of exp needed to that level from level 0 and 0 exp.
     *
     * @param level to calculate exp for
     *
     * @return total exp
     */
    int getTotalNeededExpForLevel(int level);

    /**
     * Gets the exp needed to reach that level from the current level.
     *
     * @param startLevel to start calculating at
     * @param endLevel   to calculate for
     *
     * @return exp needed
     */
    int getNeededExpForLevel(int startLevel, int endLevel);

    int getLevelAmountForExp(int exp);

    /**
     * Calculates and sets the maxp exp for the current level.
     */
    void calculateMaxExp();

    /**
     * Gets the amount of exp needed for the next level.
     *
     * @return exp needed for the next level
     * minus the one you already have
     */
    int getExpToNextLevel();

    void addExp(int exp);

    void addExp(int exp, boolean callEvent);

    void removeExp(int exp);

    void removeExp(int exp, boolean callEvent);

    void setExp(int exp, boolean callEvent);

    void addLevel(int level);

    void removeLevel(int level);

    /**
     * Checks if the player can level.
     * That means checking the current exp vs the needed exp for this level.
     *
     * @return true if player can level
     */
    boolean canLevel();

    boolean hasReachedMaxLevel();

    void saveLevelProgress();
}
