package de.raidcraft.skills.api.requirement;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.Level;
import de.raidcraft.skills.api.skill.LevelableSkill;
import org.bukkit.ChatColor;

/**
 * @author Silthus
 */
public class SkillLevelRequirement extends LevelRequirement<LevelableSkill> {

    public SkillLevelRequirement(Level<LevelableSkill> type, int requiredLevel) {

        super(type, requiredLevel);
    }

    public SkillLevelRequirement(Level<LevelableSkill> type) {

        super(type);
    }

    @Override
    public String getLongReason(Hero hero) {

        return ChatColor.RED +
                "Du musst erst deinen Skill " + ChatColor.AQUA + getLevelObject() +
                ChatColor.RED + " auf Level " + ChatColor.AQUA + getRequiredLevel() + ChatColor.RED + " bringen.";
    }

    @Override
    public String getShortReason(Hero hero) {

        return "Skill " + getLevelObject() + " auf Level " + getRequiredLevel();
    }
}
