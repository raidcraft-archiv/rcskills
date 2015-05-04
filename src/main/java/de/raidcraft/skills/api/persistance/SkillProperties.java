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

    public List<Requirement<Player>> loadRequirements(Skill skill);

    public List<Requirement<Player>> loadUseRequirements(Skill skill);

    public ItemStack[] getReagents();

    public Set<Skill> getLinkedSkills(Hero hero);

    public boolean isHidden();

    public boolean isCastable();

    public LevelFormula getLevelFormula();

    public int getMaxLevel();

    public ConfigurationSection getResourceCost(String resource);

    public int getRequiredLevel();

    public ConfigurationSection getUseExp();

    public Set<WeaponType> getRequiredWeapons();
}
