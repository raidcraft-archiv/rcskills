package de.raidcraft.skills.api.persistance;

import de.raidcraft.skills.api.level.forumla.LevelFormula;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.skill.SkillInformation;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

/**
 * @author Silthus
 */
public interface SkillProperties {

    public void loadRequirements(Skill skill);

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

    public double getCastTime();

    public double getCastTimeLevelModifier();

    public int getRange();

    public double getRangeLevelModifier();

    public double getRangeProfLevelModifier();

    public double getRangeSkillLevelModifier();

    public double getCooldown();

    public double getCooldownLevelModifier();

    public double getCooldownSkillLevelModifier();

    public double getCooldownProfLevelModifier();

    public double getSkillLevelDamageModifier();

    public double getCastTimeSkillLevelModifier();

    public double getProfLevelDamageModifier();

    public double getCastTimeProfLevelModifier();
}
