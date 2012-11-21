package de.raidcraft.skills.api.skill;

import de.raidcraft.api.inheritance.Child;
import de.raidcraft.api.inheritance.Parent;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.util.DataMap;

/**
 * @author Silthus
 */
public interface Skill extends Parent, Child<Skill>, Comparable<Skill>, SkillProperties {

    public void load(DataMap data);

    public String getName();

    public String getFriendlyName();

    public String getDescription();

    public String[] getUsage();

    public String getDescription(Hero hero);
}
