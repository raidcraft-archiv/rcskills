package de.raidcraft.skills.api.level;

import de.raidcraft.skills.api.events.RCLevelEvent;
import de.raidcraft.skills.api.persistance.LevelData;
import de.raidcraft.util.BukkitUtil;

/**
 * @author Silthus
 */
public abstract class AbstractLevel<T extends Levelable> implements Level<T> {

    private final T levelObject;
    protected int level = 1;
    protected int maxLevel = 60;
    protected int exp = 0;
    protected int maxExp;

    protected AbstractLevel(T levelObject, LevelData data) {

        this.levelObject = levelObject;
        // abort in case there are no entries yet
        if (data == null) {
            calculateMaxExp();
            return;
        }
        this.level = data.getLevel();
        this.exp = data.getExp();
        this.maxLevel = levelObject.getMaxLevel();
        calculateMaxExp();
    }

    @Override
    public T getLevelObject() {

        return levelObject;
    }

    @Override
    public int getLevel() {

        return this.level;
    }

    @Override
    public int getMaxLevel() {

        return this.maxLevel;
    }

    @Override
    public int getExp() {

        return this.exp;
    }

    @Override
    public int getMaxExp() {

        return this.maxExp;
    }

    @Override
    public int getNeededExpForLevel(int level) {
        // TODO: calculate formula for next exp max level in the respective Level implementations
        int maxExp = (int) (getMaxExp() * 1.5) + level;
        return maxExp;
    }

    @Override
    public void calculateMaxExp() {

        this.maxExp = getNeededExpForLevel(getLevel());
    }

    @Override
    public int getExpToNextLevel() {

        return this.maxExp - this.exp;
    }

    @Override
    public void addExp(int exp) {

        this.exp += exp;
        checkProgress();
    }

    @Override
    public void removeExp(int exp) {

        this.exp -= exp;
        checkProgress();
    }

    @Override
    public void setExp(int exp) {

        this.exp = exp;
        checkProgress();
    }

    @Override
    public void setLevel(int level) {

        if (level < this.level) {
            do {
                removeLevel(1);
            } while (getLevel() > level);
        } else if (level > this.level) {
            do {
                addLevel(1);
            } while (getLevel() < level);
        }
    }

    @Override
    public void addLevel(int level) {

        RCLevelEvent event = new RCLevelEvent(this, getLevel() + 1);
        BukkitUtil.callEvent(event);
        if (!event.isCancelled()) {
            increaseLevel();
            this.level += level;
            // set the exp
            setExp(getExp() - getMaxExp());
            calculateMaxExp();
            saveLevelProgress();
        }
    }

    @Override
    public void removeLevel(int level) {

        RCLevelEvent event = new RCLevelEvent(this, getLevel() - 1);
        BukkitUtil.callEvent(event);
        if (!event.isCancelled()) {
            decreaseLevel();
            this.level -= level;
            calculateMaxExp();
            saveLevelProgress();
        }
    }

    @Override
    public boolean canLevel() {

        return getExpToNextLevel() < 1 && !hasReachedMaxLevel();
    }

    @Override
    public boolean hasReachedMaxLevel() {

        return !(getLevel() < getMaxLevel());
    }

    private void checkProgress() {

        if (canLevel()) {
            // increase the level
            addLevel(1);
        } else if (getExp() < 0 && getLevel() > 0) {
            // decrease the level...
            removeLevel(1);
            // our exp are negative when we get reduced
            setExp(getMaxExp() + getExp());
        }
    }

    @SuppressWarnings("unchecked")
    private void increaseLevel() {

        levelObject.increaseLevel(this);
    }

    @SuppressWarnings("unchecked")
    private void decreaseLevel() {

        levelObject.decreaseLevel(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void saveLevelProgress() {

        levelObject.saveLevelProgress(this);
    }
}
