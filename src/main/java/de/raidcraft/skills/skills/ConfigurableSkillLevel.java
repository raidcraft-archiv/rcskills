package de.raidcraft.skills.skills;

import de.raidcraft.skills.api.level.SkillLevel;
import de.raidcraft.skills.api.persistance.LevelData;
import de.raidcraft.skills.api.skill.LevelableSkill;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Philip
 */
public class ConfigurableSkillLevel extends SkillLevel {

    private double baseMultiplier = 40.4;

    public ConfigurableSkillLevel(LevelableSkill levelObject, LevelData data, ConfigurationSection config) {

        super(levelObject, data);
        baseMultiplier = config.getDouble("exp-base", 40.4);
        calculateMaxExp();
    }

    @Override
    public int getNeededExpForLevel(int level) {

        return (int) (-.6 * Math.pow(level, 3) + baseMultiplier * Math.pow(level, 2));
    }
}
