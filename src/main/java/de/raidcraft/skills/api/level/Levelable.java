package de.raidcraft.skills.api.level;

/**
 * @author Silthus
 */
public interface Levelable<T extends Levelable> {

    public String getName();

    public AttachedLevel<T> getAttachedLevel();

    public void attachLevel(AttachedLevel<T> attachedLevel);

    public int getMaxLevel();

    public void onExpGain(int exp);

    public void onExpLoss(int exp);

    public void onLevelGain();

    public void onLevelLoss();

    public boolean isMastered();

    public void saveLevelProgress(AttachedLevel<T> attachedLevel);
}
