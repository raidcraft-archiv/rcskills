package de.raidcraft.skills.api.profession;

import de.raidcraft.api.action.requirement.RequirementResolver;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.inheritance.Child;
import de.raidcraft.skills.api.inheritance.Parent;
import de.raidcraft.skills.api.level.Levelable;
import de.raidcraft.skills.api.persistance.ProfessionProperties;
import de.raidcraft.skills.api.resource.Resource;
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Set;

/**
 * @author Silthus
 */
public interface Profession extends Levelable<Profession>, Comparable<Profession>, Parent<Profession>, Child<Profession>, RequirementResolver<Player> {

    int getId();

    String getName();

    boolean isMastered();

    String getFriendlyName();

    Hero getHero();

    boolean isActive();

    void setActive(boolean active);

    Set<Resource> getResources();

    ProfessionProperties getProperties();

    Collection<Skill> getSkills();

    void addSkill(Skill skill);

    void removeSkill(Skill skill);

    void save();

    void checkSkillsForUnlock();

    boolean hasSkill(String id);

    Skill getSkill(String id);

    int getTotalMaxLevel();

    int getTotalLevel();
}
