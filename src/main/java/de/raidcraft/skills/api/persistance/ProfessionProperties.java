package de.raidcraft.skills.api.persistance;

import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.configuration.ConfigurationSection;

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

    public String getResourceName();

    public ConfigurationSection getResourceConfig();

    public int getBaseStamina();

    public double getBaseStaminaModifier();

    public boolean isPrimary();

    public List<Skill> loadSkills(Profession profession);

    public void loadRequirements(Profession profession);
}
