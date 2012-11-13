package de.raidcraft.skills.api.profession;

import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.skill.TemplateSkill;

import java.util.Collection;

/**
 * @author Silthus
 */
public interface Profession {

    public int getId();

    public String getName();

    public String getDescription();

    public Collection<TemplateSkill> getSkills();

    public boolean hasSkill(Skill skill);
}
