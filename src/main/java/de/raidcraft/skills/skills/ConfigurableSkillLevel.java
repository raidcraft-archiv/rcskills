package de.raidcraft.skills.skills;

import de.raidcraft.skills.api.level.AbstractLevel;
import de.raidcraft.skills.api.persistance.LevelData;
import de.raidcraft.skills.api.skill.LevelableSkill;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Philip
 */
public class ConfigurableSkillLevel extends AbstractLevel<LevelableSkill> {

    private final int expBaseCount;
    private final int expIncreasePerLevel;

    public ConfigurableSkillLevel(LevelableSkill levelObject, LevelData data, ConfigurationSection config) {
        super(levelObject, data);
        expBaseCount = config.getInt("level_expBaseCount", 100);
        expIncreasePerLevel = config.getInt("level_expIncreasePerLevel", 50);
    }

    @Override
    public int getNeededExpForLevel(int level) {
        return expBaseCount + level * expIncreasePerLevel;
    }
}
