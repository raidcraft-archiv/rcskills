package de.raidcraft.skills.api.persistance;

import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.items.ArmorType;
import de.raidcraft.api.items.WeaponType;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.forumla.LevelFormula;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Silthus
 */
public interface ProfessionProperties {

    String getName();

    String getTag();

    String getFriendlyName();

    String getDescription();

    ChatColor getColor();

    boolean isEnabled();

    Profession getParentProfession(Hero hero);

    LevelFormula getLevelFormula();

    int getMaxLevel();

    ConfigurationSection getBaseHealth();

    Set<String> getResources();

    ConfigurationSection getResourceConfig(String type);

    ConfigurationSection getExpMoneyConversionRate();

    Map<String, Skill> loadSkills(Profession profession);

    List<Requirement<Player>> loadRequirements(Profession profession);

    List<Profession> loadChildren(Profession profession);

    Map<WeaponType, Integer> getAllowedWeapons();

    Map<ArmorType, Integer> getAllowedArmor();
}
