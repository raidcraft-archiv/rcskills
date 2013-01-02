package de.raidcraft.skills.api.profession;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.Levelable;
import de.raidcraft.skills.api.persistance.ProfessionProperties;
import de.raidcraft.skills.api.requirement.Unlockable;
import de.raidcraft.skills.api.skill.Skill;

import java.util.Collection;

/**
 * @author Silthus
 */
public interface Profession extends Levelable<Profession>, Comparable<Profession>, Unlockable {

    public int getId();

    public String getName();

    public Hero getHero();

    public boolean isActive();

    public void setActive(boolean active);

    public boolean isMastered();

    public ProfessionProperties getProperties();

    public Collection<Skill> getSkills();

    public Collection<Skill> getUnlockedSkills();

    public void addSkill(Skill skill);

    public void removeSkill(Skill skill);

    public void save();

    public void checkSkillsForUnlock();
}
