package de.raidcraft.skills.api.profession;

import de.raidcraft.api.inheritance.Child;
import de.raidcraft.api.inheritance.Parent;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.Levelable;
import de.raidcraft.skills.api.persistance.ProfessionProperties;
import de.raidcraft.skills.api.skill.Skill;

import java.util.Set;

/**
 * @author Silthus
 */
public interface Profession extends Parent, Child<Profession>, Levelable<Profession>, ProfessionProperties {

    public Hero getHero();

    public String getTag();

    public Set<Skill> getSkills();

    public Set<Skill> getUnlockedSkills();
}
