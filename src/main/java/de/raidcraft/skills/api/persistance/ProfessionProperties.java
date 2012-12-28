package de.raidcraft.skills.api.persistance;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;

import java.util.List;
import java.util.Set;

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

    public int getBaseMana();

    public double getBaseManaModifier();

    public int getBaseStamina();

    public double getBaseStaminaModifier();

    public boolean isPrimary();

    public List<Skill> loadSkills(Hero hero, Profession profession);

    public Set<Profession> loadStrongParents(Hero hero, Profession profession);

    public Set<Profession> loadWeakParents(Hero hero, Profession profession);
}
