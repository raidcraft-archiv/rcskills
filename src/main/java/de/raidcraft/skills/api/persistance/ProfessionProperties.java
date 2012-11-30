package de.raidcraft.skills.api.persistance;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;

import java.util.List;

/**
 * @author Silthus
 */
public interface ProfessionProperties {

    public String getName();

    public String getTag();

    public String getFriendlyName();

    public String getDescription();

    public int getMaxLevel();

    public int getBaseHealth();

    public double getBaseHealthModifier();

    public boolean isPrimary();

    public List<Skill> loadSkills(Hero hero, Profession profession);
}
