package de.raidcraft.skills.api;

/**
 * @author Silthus
 */
public interface Skill {

    public int getId();

    public String getName();

    public String getDescription();

    public String[] getUsage();

    public double getCost();
}
