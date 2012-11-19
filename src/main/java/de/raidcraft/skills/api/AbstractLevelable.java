package de.raidcraft.skills.api;

import de.raidcraft.skills.api.events.RCLevelEvent;
import de.raidcraft.skills.api.persistance.LevelData;
import de.raidcraft.util.BukkitUtil;

/**
 * @author Silthus
 */
public abstract class AbstractLevelable implements Levelable {

    protected int level;
    protected int maxLevel;
    protected int exp;
    protected int maxExp;

    protected AbstractLevelable(LevelData data) {

        // abort in case there are no entries yet
        if (data == null) {
            this.maxExp = calculateMaxExp();
            return;
        }
        this.level = data.getLevel();
        this.maxLevel = data.getMaxLevel();
        this.exp = data.getExp();
        this.maxExp = calculateMaxExp();
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
    public int calculateMaxExp() {
        // TODO: calculate formula for next exp max level
        maxExp = (int) (maxExp * 1.5);
        return maxExp;
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

    @Override
    public void increaseLevel() {
        // override if needed
    }

    @Override
    public void decreaseLevel() {
        // override if needed
    }
}
