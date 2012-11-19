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

    public int getMaxExp();

    public int calculateMaxExp();

    public int getExpToNextLevel();

    public void addExp(int exp);

    public void removeExp(int exp);

    public void setExp(int exp);

    public void setLevel(int level);

    public void addLevel(int level);

    public void removeLevel(int level);

    public boolean canLevel();

    public boolean hasReachedMaxLevel();

    public void increaseLevel();

    public void decreaseLevel();

    public void saveLevelProgress();
}
