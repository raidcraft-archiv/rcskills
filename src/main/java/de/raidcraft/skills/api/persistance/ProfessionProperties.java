package de.raidcraft.skills.api.persistance;

import de.raidcraft.skills.api.skill.Skill;

import java.util.Set;

/**
 * @author Silthus
 */
public interface ProfessionProperties {

    public int getId();

    public String getName();

    public String getFriendlyName();

    public String getDescription();

    public boolean isActive();

    public boolean isMastered();

    public int getMaxLevel();

    public Set<Skill> getSkills();
}
