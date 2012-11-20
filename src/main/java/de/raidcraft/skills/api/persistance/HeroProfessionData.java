package de.raidcraft.skills.api.persistance;

import de.raidcraft.skills.api.skill.Skill;

import java.util.Collection;

/**
 * @author Silthus
 */
public abstract class HeroProfessionData {

    protected boolean active;
    protected boolean mastered;
    protected Collection<Skill> gainedSkills;

    public boolean isActive() {

        return active;
    }

    public boolean isMastered() {

        return mastered;
    }

    public Collection<Skill> getGainedSkills() {

        return gainedSkills;
    }
}
