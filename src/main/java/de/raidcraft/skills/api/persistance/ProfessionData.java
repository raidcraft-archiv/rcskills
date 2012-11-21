package de.raidcraft.skills.api.persistance;

import de.raidcraft.skills.api.skill.Skill;

import java.util.Set;

/**
 * @author Silthus
 */
public interface ProfessionData {

    public String getName();

    public String getFriendlyName();

    public String getDescription();

    public boolean isActive();

    public boolean isMastered();

    public LevelData getLevelData();

    public Set<Skill> getSkills();
}
