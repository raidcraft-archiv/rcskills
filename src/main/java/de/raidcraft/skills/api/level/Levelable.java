package de.raidcraft.skills.api.level;

/**
 * @author Silthus
 */
public interface Levelable<T extends Levelable> {

    public Level<T> getLevel();

    public void attachLevel(Level<T> level);

    public int getMaxLevel();

    public void onExpGain(int exp);

    public void onExpLoss(int exp);

    public void onLevelGain();

    public void onLevelLoss();

    public void saveLevelProgress(Level<T> level);
}
