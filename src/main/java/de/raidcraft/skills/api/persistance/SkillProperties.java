package de.raidcraft.skills.api.persistance;

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

    public ConfigurationSection getData();

    public SkillInformation getInformation();

    public int getMaxLevel();

    public int getManaCost();

    public double getManaCostLevelModifier();

    public int getStaminaCost();

    public double getStaminaCostLevelModifier();

    public int getHealthCost();

    public double getHealthCostLevelModifier();

    public int getRequiredLevel();

    public int getDamage();

    public double getDamageLevelModifier();

    public int getCastTime();

    public double getCastTimeLevelModifier();

    public double getCooldown();

    public double getCooldownLevelModifier();

    public double getCooldownSkillLevelModifier();

    public double getCooldownProfLevelModifier();

    public double getSkillLevelDamageModifier();

    public double getManaCostSkillLevelModifier();

    public double getStaminaCostSkillLevelModifier();

    public double getHealthCostSkillLevelModifier();

    public double getCastTimeSkillLevelModifier();

    public double getProfLevelDamageModifier();

    public double getManaCostProfLevelModifier();

    public double getStaminaCostProfLevelModifier();

    public double getHealthCostProfLevelModifier();

    public double getCastTimeProfLevelModifier();
}
