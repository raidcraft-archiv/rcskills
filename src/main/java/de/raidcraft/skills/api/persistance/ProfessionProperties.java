package de.raidcraft.skills.api.persistance;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.configuration.ConfigurationSection;

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

    public Profession getParentProfession(Hero hero);

    public int getMaxLevel();

    public int getBaseHealth();

    public double getBaseHealthModifier();

    public Set<String> getResources();

    public ConfigurationSection getResourceConfig(String type);

    public boolean isPrimary();

    public List<Skill> loadSkills(Profession profession);

    public void loadRequirements(Profession profession);

    public List<Profession> loadChildren(Hero hero);
}
