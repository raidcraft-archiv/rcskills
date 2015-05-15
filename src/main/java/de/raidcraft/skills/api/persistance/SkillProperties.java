package de.raidcraft.skills.api.persistance;

import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.items.WeaponType;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.forumla.LevelFormula;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.skill.SkillInformation;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

/**
 * @author Silthus
 */
public interface SkillProperties extends AbilityProperties<SkillInformation> {

    List<Requirement<Player>> loadRequirements(Skill skill);

    List<Requirement<Player>> loadUseRequirements(Skill skill);

    ItemStack[] getReagents();

    Set<Skill> getLinkedSkills(Hero hero);

    boolean isHidden();

    boolean isCastable();

    LevelFormula getLevelFormula();

    int getMaxLevel();

    ConfigurationSection getResourceCost(String resource);

    int getRequiredLevel();

    ConfigurationSection getUseExp();

    Set<WeaponType> getRequiredWeapons();
}
