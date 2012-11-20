package de.raidcraft.skills.api.profession;

import de.raidcraft.api.inheritance.Parent;
import de.raidcraft.skills.api.Levelable;
import de.raidcraft.skills.api.persistance.ProfessionData;
import de.raidcraft.skills.api.skill.Skill;

import java.util.Collection;

/**
 * @author Silthus
 */
public interface Profession extends Parent, Levelable {

    public void load(ProfessionData data);

    public int getId();

    public String getName();

    public String getFriendlyName();

    public String getTag();

    public String getDescription();

    public Collection<Skill> getAllSkills();

    public Collection<Skill> getUngainedSkills();

    public Collection<Skill> getGainedSkills();

    public boolean isActive();

    public boolean isMastered();
}
