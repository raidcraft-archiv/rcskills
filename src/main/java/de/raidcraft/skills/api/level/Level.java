package de.raidcraft.skills.api.level;

/**
 * @author Silthus
 */
public interface Level<T extends Levelable> {

    public T getLevelObject();

    public int getLevel();

    public int getMaxLevel();

    public int getExp();

    /**
     * Gets the maximum amount of exp for this level.
     *
     * @return max exp for level
     */
    public int getMaxExp();

    /**
     * Calculates how much exp is needed for the given level.
     * Will _not_ set the max exp.
     *
     * @return exp needed for the given level
     */
    public int getNeededExpForLevel(int level);

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

    public void removeExp(int exp);

    public void setExp(int exp);

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
