package de.raidcraft.skills.api.profession;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.resource.Resource;
import de.raidcraft.skills.api.level.Levelable;
import de.raidcraft.skills.api.persistance.ProfessionProperties;
import de.raidcraft.skills.api.requirement.Unlockable;
import de.raidcraft.skills.api.skill.Skill;

import java.util.Collection;
import java.util.Set;

/**
 * @author Silthus
 */
public interface Profession extends Levelable<Profession>, Comparable<Profession>, Unlockable {

    public int getId();

    public String getName();

    public String getFriendlyName();

    public Hero getHero();

    public boolean isActive();

    public void setActive(boolean active);

    public boolean isMastered();

    public Resource getResource(String type);

    public Set<Resource> getResources();

    public ProfessionProperties getProperties();

    public Collection<Skill> getSkills();

    public void addSkill(Skill skill);

    public void removeSkill(Skill skill);

    public void save();

    public void checkSkillsForUnlock();
}
