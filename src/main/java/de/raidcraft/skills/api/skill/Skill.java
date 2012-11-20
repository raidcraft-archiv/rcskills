package de.raidcraft.skills.api.skill;

import de.raidcraft.api.inheritance.Child;
import de.raidcraft.api.inheritance.Parent;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillData;
import de.raidcraft.skills.api.profession.Profession;

/**
 * @author Silthus
 */
public interface Skill extends Parent, Child<Skill>, Comparable<Skill> {

    public void load(SkillData data);

    public int getId();

    public String getName();

    public String getFriendlyName();

    public String getDescription();

    public String getDescription(Hero hero);

    public String[] getUsage();

    public int getRequiredLevel();

    public Profession getProfession();

    public boolean hasUsePermission(Hero hero);
}
