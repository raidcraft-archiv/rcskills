package de.raidcraft.skills.api.persistance;

import de.raidcraft.api.config.DataMap;
import de.raidcraft.skills.api.skill.SkillInformation;
import org.bukkit.inventory.ItemStack;

/**
 * @author Silthus
 */
public interface SkillProperties {

    public String getName();

    public String getFriendlyName();

    public String getDescription();

    public String[] getUsage();

    public ItemStack[] getReagents();

    public DataMap getData();

    public SkillInformation getInformation();

    public int getMaxLevel();

    public int getManaCost();

    public double getManaLevelModifier();

    public int getStaminaCost();

    public double getStaminaLevelModifier();

    public int getHealthCost();

    public double getHealthLevelModifier();

    public int getRequiredLevel();

    public int getDamage();

    public double getDamageLevelModifier();

    public int getCastTime();

    public double getCastTimeLevelModifier();

    public double getSkillLevelDamageModifier();

    public double getSkillLevelManaCostModifier();

    public double getSkillLevelStaminaCostModifier();

    public double getSkillLevelHealthCostModifier();

    public double getSkillLevelCastTimeModifier();

    public double getProfLevelDamageModifier();

    public double getProfLevelManaCostModifier();

    public double getProfLevelStaminaCostModifier();

    public double getProfLevelHealthCostModifier();

    public double getProfLevelCastTimeModifier();
}
