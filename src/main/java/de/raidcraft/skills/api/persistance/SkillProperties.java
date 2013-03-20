package de.raidcraft.skills.api.persistance;

import de.raidcraft.api.requirement.Requirement;
import de.raidcraft.skills.api.level.forumla.LevelFormula;
import de.raidcraft.skills.api.resource.Resource;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.skill.SkillInformation;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @author Silthus
 */
public interface SkillProperties {

    public List<Requirement<Skill>> loadRequirements(Skill skill);

    public List<Requirement<Skill>> loadUseRequirements(Skill skill);

    public String getName();

    public String getFriendlyName();

    public String getDescription();

    public String[] getUsage();

    public ItemStack[] getReagents();

    public boolean isHidden();

    public boolean isEnabled();

    public void setEnabled(boolean enabled);

    public boolean canUseInCombat();

    public boolean canUseOutOfCombat();

    public ConfigurationSection getData();

    public LevelFormula getLevelFormula();

    public SkillInformation getInformation();

    public int getMaxLevel();

    public Resource.Type getResourceType(String resource);

    public boolean isVariableResourceCost(String resource);

    public double getResourceCost(String resource);

    public double getResourceCostLevelModifier(String resource);

    public double getResourceCostSkillLevelModifier(String resource);

    public double getResourceCostProfLevelModifier(String resource);

    public int getRequiredLevel();

    public ConfigurationSection getDamage();

    public ConfigurationSection getCastTime();

    public ConfigurationSection getRange();

    public ConfigurationSection getCooldown();
}
