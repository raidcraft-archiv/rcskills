package de.raidcraft.skills.api.hero;

import de.raidcraft.skills.api.hero.Hero;

/**
 * @author Silthus
 */
public interface Attribute {

    public int getId();

    public String getName();

    public String getFriendlyName();

    public Hero getHero();

    public int getValue();

    public void setValue(int value);

    public void save();
}
