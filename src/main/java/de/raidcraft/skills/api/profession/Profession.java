package de.raidcraft.skills.api.profession;

import de.raidcraft.skills.api.skill.Skill;

import java.util.Collection;

/**
 * @author Silthus
 */
public interface Profession {

    public int getId();

    public String getName();

    public String getDescription();

    public Collection<Skill> getSkills();

    public boolean hasSkill(Skill skill);
}
