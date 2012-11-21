package de.raidcraft.skills.api.profession;

import de.raidcraft.api.inheritance.Child;
import de.raidcraft.api.inheritance.Parent;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.Levelable;
import de.raidcraft.skills.api.skill.Skill;

import java.util.Collection;

/**
 * @author Silthus
 */
public interface Profession extends Parent, Child<Profession>, Levelable<Profession> {

    public Hero getHero();

    public String getName();

    public String getFriendlyName();

    public String getTag();

    public String getDescription();

    public boolean isActive();

    public boolean isMastered();

    public Collection<Skill> getSkills();
}
