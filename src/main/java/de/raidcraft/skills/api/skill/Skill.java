package de.raidcraft.skills.api.skill;

import de.raidcraft.api.inheritance.Child;
import de.raidcraft.api.inheritance.Parent;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.util.DataMap;

/**
 * @author Silthus
 */
public interface Skill extends Parent, Child<Skill>, Comparable<Skill> {

    public void load(DataMap data);

    public int getId();

    public String getName();

    public String getFriendlyName();

    public String getDescription();

    public String getDescription(Hero hero);

    public String[] getUsage();

    public int getRequiredLevel();
}
