package de.raidcraft.skills.api.effect;

/**
 * @author Silthus
 */
public interface Stackable {

    int getStacks();

    void setStacks(int stacks);

    int getMaxStacks();
}
