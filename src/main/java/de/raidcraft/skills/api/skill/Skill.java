package de.raidcraft.skills.api.skill;

import de.raidcraft.api.inheritance.Child;
import de.raidcraft.api.inheritance.Parent;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.util.DataMap;

/**
 * @author Silthus
 */
public interface Skill extends Parent, Child<Skill>, Comparable<Skill> {

    public void load(DataMap data);

    public Hero getHero();

    public String getDescription(Hero hero);

    public boolean isActive();

    public boolean isUnlocked();

    public double getTotalDamage();

    public double getTotalManaCost();

    public double getTotalStaminaCost();

    public double getTotalHealthCost();

    public SkillProperties getProperties();

    public Profession getProfession() throws UnknownProfessionException;
}
