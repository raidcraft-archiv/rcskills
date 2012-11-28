package de.raidcraft.skills.api.profession;

import de.raidcraft.api.inheritance.Child;
import de.raidcraft.api.inheritance.Parent;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.Levelable;
import de.raidcraft.skills.api.persistance.ProfessionProperties;
import de.raidcraft.skills.api.skill.Skill;

import java.util.Collection;

/**
 * @author Silthus
 */
public interface Profession extends Parent, Child<Profession>, Levelable<Profession> {

    public int getId();

    public Hero getHero();

    public boolean isActive();

    public boolean isMastered();

    public ProfessionProperties getProperties();

    public Collection<Skill> getSkills();

    public Collection<Skill> getUnlockedSkills();
}
