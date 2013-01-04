package de.raidcraft.skills.api.level;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.api.events.RCLevelEvent;
import de.raidcraft.skills.api.persistance.LevelData;

/**
 * @author Silthus
 */
public abstract class AbstractLevel<T extends Levelable> implements Level<T> {

    private final T levelObject;
    protected int level = 1;
    protected int maxLevel = 60;
    protected int exp = 0;
    protected int maxExp;

    public AbstractLevel(T levelObject, LevelData data) {

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

    public AbstractLevel(T levelObject) {

        this(levelObject, null);
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
    public void calculateMaxExp() {

        this.maxExp = getNeededExpForLevel(getLevel());
    }

    @Override
    public int getExpToNextLevel() {

        return this.maxExp - this.exp;
    }

    @Override
    public void addExp(int exp) {

        if (getLevelObject().isMastered()) {
            return;
        }
        this.exp += exp;
        getLevelObject().onExpGain(exp);
        checkProgress();
    }

    @Override
    public void removeExp(int exp) {

        this.exp -= exp;
        getLevelObject().onExpLoss(exp);
        checkProgress();
    }

    @Override
    public void setExp(int exp) {

        if (this.exp < exp) {
            getLevelObject().onExpGain(exp - this.exp);
        } else {
            getLevelObject().onExpLoss(this.exp - exp);
        }
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

        RCLevelEvent event = new RCLevelEvent<>(levelObject, getLevel(), level);
        RaidCraft.callEvent(event);
        if (!event.isCancelled()) {
            for (int i = 0; i < level; i++) {
                if (!hasReachedMaxLevel()) {
                    increaseLevel();
                }
            }
        }
    }

    @Override
    public void removeLevel(int level) {

        RCLevelEvent event = new RCLevelEvent<>(levelObject, getLevel(), level);
        RaidCraft.callEvent(event);
        if (!event.isCancelled()) {
            for (int i = 0; i < level; i++) {
                if (1 < getLevel()) {
                    decreaseLevel();
                }
            }
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

        this.level++;
        // set the exp
        setExp(getExp() - getMaxExp());
        calculateMaxExp();
        saveLevelProgress();
        getLevelObject().onLevelGain();
    }

    @SuppressWarnings("unchecked")
    private void decreaseLevel() {

        this.level--;
        calculateMaxExp();
        saveLevelProgress();
        levelObject.onLevelLoss();
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void saveLevelProgress() {

        levelObject.saveLevelProgress(this);
    }
}
