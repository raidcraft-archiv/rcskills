package de.raidcraft.skills.skills;

import de.raidcraft.skills.api.level.SkillLevel;
import de.raidcraft.skills.api.persistance.LevelData;
import de.raidcraft.skills.api.skill.LevelableSkill;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Philip
 */
public class ConfigurableSkillLevel extends SkillLevel {

    private final int expBaseCount;
    private final int expIncreasePerLevel;

    public ConfigurableSkillLevel(LevelableSkill levelObject, LevelData data, ConfigurationSection config) {

        super(levelObject, data);
        expBaseCount = config.getInt("level.exp-base", 100);
        expIncreasePerLevel = config.getInt("level.exp-per-level", 50);
    }

    @Override
    public int getNeededExpForLevel(int level) {

        return expBaseCount + level * expIncreasePerLevel;
    }
}
