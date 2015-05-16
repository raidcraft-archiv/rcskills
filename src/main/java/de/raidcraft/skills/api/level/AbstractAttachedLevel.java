package de.raidcraft.skills.api.level;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.api.events.RCExpGainEvent;
import de.raidcraft.skills.api.events.RCLevelEvent;
import de.raidcraft.skills.api.level.forumla.LevelFormula;
import de.raidcraft.skills.api.persistance.LevelData;

/**
 * @author Silthus
 */
public abstract class AbstractAttachedLevel<T extends Levelable> implements AttachedLevel<T> {

    private final T levelObject;
    private final LevelFormula formula;
    protected int level = 1;
    protected int maxLevel = 60;
    protected int exp = 0;
    protected int maxExp;

    public AbstractAttachedLevel(T levelObject, LevelFormula formula) {

        this(levelObject, formula, null);
    }

    public AbstractAttachedLevel(T levelObject, LevelFormula formula, LevelData data) {

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

    @Override
    public T getLevelObject() {

        return levelObject;
    }

    @Override
    public int getLevel() {

        return this.level;
    }

    @Override
    public void setLevel(int level) {

        if (hasReachedMaxLevel()) return;
        if (level > getMaxLevel()) level = getMaxLevel();
        if (level < 1) level = 1;
        if (level == this.level) return;

        RCLevelEvent event = new RCLevelEvent(getLevelObject(), getLevel(), level);
        RaidCraft.callEvent(event);

        if (!event.isCancelled()) {
            int oldLevel = this.level;
            this.level = level;

            if (level > oldLevel) increaseLevel();
            if (level < oldLevel) decreaseLevel();
        }
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
    public void setExp(int exp) {

        setExp(exp, true);
    }

    @Override
    public LevelFormula getFormula() {

        return formula;
    }

    @Override
    public int getMaxExp() {

        return this.maxExp;
    }

    @Override
    public final int getTotalNeededExpForLevel(int level) {

        int exp = 0;
        for (int i = 1; i <= level; i++) {
            exp += getFormula().getNeededExpForLevel(i);
        }
        return exp;
    }

    @Override
    public int getNeededExpForLevel(int startLevel, int endLevel) {

        if (endLevel > getMaxLevel()) {
            endLevel = getMaxLevel();
        }
        int neededExp = 0;
        for (int i = startLevel; i <= endLevel; i++) {
            neededExp += getFormula().getNeededExpForLevel(i);
        }
        return neededExp;
    }

    @Override
    public int getLevelAmountForExp(int exp) {

        int level = 0;
        while (exp >= 0) {
            exp -= getFormula().getNeededExpForLevel(getLevel() + level);
            if (exp < 0) {
                break;
            }
            level++;
        }
        return level;
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
    public void addExp(int exp) {

        addExp(exp, true);
    }

    @Override
    public void addExp(int exp, boolean callEvent) {

        if (exp == 0) {
            return;
        }
        RCExpGainEvent event = new RCExpGainEvent(this, exp);
        if (callEvent) RaidCraft.callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        this.exp += event.getGainedExp();
        getLevelObject().onExpGain(exp);
        checkProgress();
    }

    @Override
    public void removeExp(int exp) {

        removeExp(exp, true);
    }

    @Override
    public void removeExp(int exp, boolean callEvent) {

        if (exp == 0) {
            return;
        }
        this.exp -= exp;
        getLevelObject().onExpLoss(exp);
        checkProgress();
    }

    @Override
    public void setExp(int exp, boolean callEvent) {

        if (this.exp < exp) {
            RCExpGainEvent event = new RCExpGainEvent(this, exp - this.exp);
            if (callEvent) RaidCraft.callEvent(event);
            if (event.isCancelled()) {
                return;
            }
            exp = this.exp + event.getGainedExp();
            getLevelObject().onExpGain((this.exp + event.getGainedExp()) - this.exp);
        } else {
            getLevelObject().onExpLoss(this.exp - exp);
        }
        this.exp = exp;
        checkProgress();
    }

    @Override
    public void addLevel(int level) {

        if (level == 0) {
            return;
        }
        setLevel(getLevel() + level);
    }

    @Override
    public void removeLevel(int level) {

        if (level == 0) {
            return;
        }
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

    @Override
    @SuppressWarnings("unchecked")
    public void saveLevelProgress() {

        levelObject.saveLevelProgress(this);
    }

    private void checkProgress() {

        if (canLevel()) {
            int newLevel = getLevel() + getLevelAmountForExp(getExp());
            if (newLevel > getMaxLevel()) {
                newLevel = getMaxLevel();
            }
            // lets get the total needed exp from the old level to the new level-1
            int neededExp = getNeededExpForLevel(getLevel(), newLevel - 1);
            // increase the level
            setLevel(newLevel);
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
}
