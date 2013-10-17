package de.raidcraft.skills.api.persistance;

import de.raidcraft.api.items.ArmorType;
import de.raidcraft.api.items.WeaponType;
import de.raidcraft.api.requirement.Requirement;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.forumla.LevelFormula;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Silthus
 */
public interface ProfessionProperties {

    public String getName();

    public String getTag();

    public String getFriendlyName();

    public String getDescription();

    public boolean isEnabled();

    public Profession getParentProfession(Hero hero);

    public LevelFormula getLevelFormula();

    public int getMaxLevel();

    public ConfigurationSection getBaseHealth();

    public Set<String> getResources();

    public ConfigurationSection getResourceConfig(String type);

    public ConfigurationSection getExpMoneyConversionRate();

    public Map<String, Skill> loadSkills(Profession profession);

    public List<Requirement<Hero>> loadRequirements(Profession profession);

    public List<Profession> loadChildren(Profession profession);

    public Map<WeaponType, Integer> getAllowedWeapons();

    public Map<ArmorType, Integer> getAllowedArmor();
}
