package de.raidcraft.skills.api.level;

/**
 * @author Silthus
 */
public interface Levelable<T extends Levelable> {

    public Level<T> getLevel();

    public int getMaxLevel();

    public void attachLevel(Level<T> level);

    public void onLevelUp(Level<T> level);

    public void onLevelDown(Level<T> level);

    public void saveLevelProgress(Level<T> level);
}
