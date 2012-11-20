package de.raidcraft.skills.api;

import de.raidcraft.skills.api.hero.Hero;

/**
 * @author Silthus
 */
public interface Levelable {

    public Hero getHero();

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
     * minus the one you already have
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

    /**
     * This method is called _after_ the player has leveled up.
     * Should be used to adjust skill damage or do other stuff.
     */
    public void increaseLevel();

    /**
     * This method is called _after_ the player lost a level.
     * Should be used to adjust skill damage or do other stuff.
     */
    public void decreaseLevel();

    /**
     * Saves the progress of the level.
     */
    public void saveLevelProgress();
}
