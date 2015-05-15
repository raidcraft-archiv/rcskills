package de.raidcraft.skills.api.level;

/**
 * @author Silthus
 */
public interface Levelable<T extends Levelable> {

    String getName();

    AttachedLevel<T> getAttachedLevel();

    void attachLevel(AttachedLevel<T> attachedLevel);

    int getMaxLevel();

    void onExpGain(int exp);

    void onExpLoss(int exp);

    void onLevelGain();

    void onLevelLoss();

    boolean isMastered();

    void saveLevelProgress(AttachedLevel<T> attachedLevel);
}
