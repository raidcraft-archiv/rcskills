package de.raidcraft.skills.api.hero;

/**
 * @author Silthus
 */
public interface Attribute {

    public int getId();

    public String getName();

    public String getFriendlyName();

    public Hero getHero();

    public void setBaseValue(int value);

    public int getBaseValue();

    public int getCurrentValue();

    public void setCurrentValue(int value);

    public double getDamageModifier();

    public double getHealthModifier();

    public void save();
}
