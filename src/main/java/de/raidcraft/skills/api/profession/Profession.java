package de.raidcraft.skills.api.profession;

import de.raidcraft.api.inheritance.Child;
import de.raidcraft.api.inheritance.Parent;
import de.raidcraft.skills.api.skill.Skill;

import java.util.Collection;

/**
 * @author Silthus
 */
public interface Profession extends Parent, Child<Profession> {

    public int getId();

    public String getName();

    public String getFriendlyName();

    public String getTag();

    public String getDescription();

    public Collection<Skill> getSkills();
}
