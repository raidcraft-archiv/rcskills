package de.raidcraft.skills.api.level;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.LevelData;

/**
 * @author Silthus
 */
public class ExpPool extends AbstractLevel<Hero> {

    public ExpPool(Hero levelObject, LevelData data) {

        super(levelObject, data);
    }

    @Override
    public int getLevel() {

        return 1;
    }

    @Override
    public int getMaxLevel() {

        return 1;
    }

    @Override
    public int getMaxExp() {

        return 999999999;
    }

    @Override
    public void calculateMaxExp() {

    }

    @Override
    public int getExpToNextLevel() {

        return 1;
    }

    @Override
    public void setLevel(int level) {

    }

    @Override
    public void addLevel(int level) {

    }

    @Override
    public void removeLevel(int level) {

    }

    @Override
    public boolean canLevel() {

        return false;
    }

    @Override
    public boolean hasReachedMaxLevel() {

        return true;
    }

    @Override
    public int getNeededExpForLevel(int level) {

        return 1;
    }
}
