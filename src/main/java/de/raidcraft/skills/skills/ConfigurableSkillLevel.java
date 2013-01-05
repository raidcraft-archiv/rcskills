package de.raidcraft.skills.skills;

import de.raidcraft.skills.api.level.SkillLevel;
import de.raidcraft.skills.api.persistance.LevelData;
import de.raidcraft.skills.api.skill.LevelableSkill;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Philip
 */
public class ConfigurableSkillLevel extends SkillLevel {

    public ConfigurableSkillLevel(LevelableSkill levelObject, LevelData data, ConfigurationSection config) {

        super(levelObject, data);
        calculateMaxExp();
    }

    @Override
    public int getNeededExpForLevel(int level) {

        return (int) (-.8 * Math.pow(level, 3) + 40.4 * Math.pow(level, 2));
    }
}
