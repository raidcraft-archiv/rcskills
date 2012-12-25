package de.raidcraft.skills.api.level;

/**
 * @author Silthus
 */
public interface Levelable<T extends Levelable> {

    public Level<T> getLevel();

    public int getMaxLevel();

    public void onExpGain(int exp);

    public void onExpLoss(int exp);

    public void onLevelGain(int level);

    public void onLevelLoss(int level);

    public void saveLevelProgress(Level<T> level);
}
