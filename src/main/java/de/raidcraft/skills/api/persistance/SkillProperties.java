package de.raidcraft.skills.api.persistance;

import de.raidcraft.skills.api.skill.SkillInformation;
import de.raidcraft.skills.api.skill.SkillType;

/**
 * @author Silthus
 */
public interface SkillProperties {

    public int getId();

    public SkillInformation getInformation();

    public int getMaxLevel();

    public String getName();

    public String getFriendlyName();

    public String getDescription();

    public String[] getUsage();

    public SkillType[] getSkillTypes();

    public boolean isUnlocked();

    public int getManaCost();

    public double getManaLevelModifier();

    public int getStaminaCost();

    public double getStaminaLevelModifier();

    public int getHealthCost();

    public double getHealthLevelModifier();

    public int getRequiredLevel();

    public int getDamage();

    public double getDamageLevelModifier();

    public double getCastTime();

    public double getCastTimeLevelModifier();

    public double getDuration();

    public double getDurationLevelModifier();

    public double getSkillLevelDamageModifier();

    public double getSkillLevelManaCostModifier();

    public double getSkillLevelStaminaCostModifier();

    public double getSkillLevelHealthCostModifier();

    public double getSkillLevelCastTimeModifier();

    public double getSkillLevelDurationModifier();

    public double getProfLevelDamageModifier();

    public double getProfLevelManaCostModifier();

    public double getProfLevelStaminaCostModifier();

    public double getProfLevelHealthCostModifier();

    public double getProfLevelCastTimeModifier();

    public double getProfLevelDurationModifier();
}
