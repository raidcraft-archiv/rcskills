package de.raidcraft.skills.api;

/**
 * @author Silthus
 */
public interface LevelObject<T extends LevelObject> {

    public Level<T> getLevel();

    public void attachLevel(Level<T> level);

    public void increaseLevel(Level<T> level);

    public void decreaseLevel(Level<T> level);

    public void saveLevelProgress(Level<T> level);
}
