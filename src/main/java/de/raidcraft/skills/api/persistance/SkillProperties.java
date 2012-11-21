package de.raidcraft.skills.api.persistance;

/**
 * @author Silthus
 */
public interface SkillProperties {

    public int getId();

    public String getFriendlyName();

    public String getDescription();

    public String[] getUsage();

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
}
