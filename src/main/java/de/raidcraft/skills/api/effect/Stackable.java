package de.raidcraft.skills.api.effect;

/**
 * @author Silthus
 */
public interface Stackable {

    public int getStacks();

    public void setStacks(int stacks);

    public int getMaxStacks();
}
