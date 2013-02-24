package de.raidcraft.skills.api.persistance;

import de.raidcraft.api.requirement.Requirement;
import de.raidcraft.skills.api.level.forumla.LevelFormula;
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

    public int getResourceCost(String resource);

    public double getResourceCostLevelModifier(String resource);

    public double getResourceCostSkillLevelModifier(String resource);

    public double getResourceCostProfLevelModifier(String resource);

    public int getRequiredLevel();

    public int getDamage();

    public double getDamageLevelModifier();

    public double getDamageProfLevelModifier();

    public double getDamageSkillLevelModifier();

    public double getDamageResourceModifier(String resouce);

    public double getCastTime();

    public double getCastTimeLevelModifier();

    public double getCastTimeProfLevelModifier();

    public double getCastTimeSkillLevelModifier();

    public double getCastTimeResourceModifier(String resouce);

    public int getRange();

    public double getRangeLevelModifier();

    public double getRangeProfLevelModifier();

    public double getRangeSkillLevelModifier();

    public double getRangeResourceModifier(String resouce);

    public double getCooldown();

    public double getCooldownLevelModifier();

    public double getCooldownSkillLevelModifier();

    public double getCooldownProfLevelModifier();

    public double getCooldownResourceModifier(String resouce);
}
