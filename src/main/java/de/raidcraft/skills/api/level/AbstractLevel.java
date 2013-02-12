package de.raidcraft.skills.api.level;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.api.events.RCLevelEvent;
import de.raidcraft.skills.api.level.forumla.LevelFormula;
import de.raidcraft.skills.api.persistance.LevelData;

/**
 * @author Silthus
 */
public abstract class AbstractLevel<T extends Levelable> implements Level<T> {

    private final T levelObject;
    private final LevelFormula formula;
    protected int level = 1;
    protected int maxLevel = 60;
    protected int exp = 0;
    protected int maxExp;

    public AbstractLevel(T levelObject, LevelFormula formula, LevelData data) {

        this.levelObject = levelObject;
        this.formula = formula;
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

    public AbstractLevel(T levelObject, LevelFormula formula) {

        this(levelObject, formula, null);
    }

    @Override
    public T getLevelObject() {

        return levelObject;
    }

    @Override
    public LevelFormula getFormula() {

        return formula;
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

        maxExp = getFormula().getNeededExpForLevel(getLevel());
        if (maxExp == 0) maxExp = 1;
    }

    @Override
    public int getExpToNextLevel() {

        return this.maxExp - this.exp;
    }

    @Override
    public final int getTotalNeededExpForLevel(int level) {

        int exp = 0;
        for (int i = 1; i <= level; i++) {
            exp += getFormula().getNeededExpForLevel(i);
        }
        return exp;
    }

    private int getLevelAmountForExp(int exp) {

        int level = 0;
        while (level <= getMaxLevel() && exp >= 0) {
            exp -= getFormula().getNeededExpForLevel(getLevel() + level);
            if (exp >= 0) level++;
        }
        return level;
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

        if (hasReachedMaxLevel()) return;
        if (level > getMaxLevel()) level = getMaxLevel();
        if (level < 1) level = 1;
        if (level == this.level) return;

        RCLevelEvent event = new RCLevelEvent<>(levelObject, getLevel(), level);
        RaidCraft.callEvent(event);

        if (!event.isCancelled()) {
            int oldLevel = this.level;
            this.level = level;

            if (level > oldLevel) increaseLevel();
            if (level < oldLevel) decreaseLevel();
        }
    }

    @Override
    public void addLevel(int level) {

        setLevel(getLevel() + level);
    }

    @Override
    public void removeLevel(int level) {

        setLevel(getLevel() - level);
    }

    @Override
    public boolean canLevel() {

        return getExpToNextLevel() < 1 && !hasReachedMaxLevel();
    }

    @Override
    public boolean hasReachedMaxLevel() {

        return getMaxLevel() <= getLevel();
    }

    private void checkProgress() {

        if (canLevel()) {
            int oldLevel = getLevel();
            int newLevel = getLevel() + getLevelAmountForExp(getExp());
            // increase the level
            setLevel(newLevel);
            // lets get the total needed exp from the old level to the new level-1
            int neededExp = getFormula().getNeededExpForLevel(oldLevel);
            // set the exp
            setExp(getExp() - neededExp);
        } else if (getExp() < 0 && getLevel() > 0) {
            // decrease the level...
            removeLevel(1);
            // our exp are negative when we get reduced
            setExp(getMaxExp() + getExp());
        }
    }

    private void increaseLevel() {

        calculateMaxExp();
        saveLevelProgress();
        getLevelObject().onLevelGain();
    }

    private void decreaseLevel() {

        calculateMaxExp();
        saveLevelProgress();
        getLevelObject().onLevelLoss();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void saveLevelProgress() {

        levelObject.saveLevelProgress(this);
    }
}
