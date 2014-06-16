package de.raidcraft.skills.requirement;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.requirement.RequirementInformation;
import de.raidcraft.api.requirement.RequirementResolver;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.Levelable;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.LevelableSkill;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
@RequirementInformation("skill-level")
public class SkillLevelRequirement extends LevelRequirement {

    private LevelableSkill requiredSkill;

    public SkillLevelRequirement(RequirementResolver<Hero> resolver, ConfigurationSection config) {

        super(resolver, config);
    }

    @Override
    protected Levelable getLevelable() {

        return requiredSkill;
    }

    @Override
    protected void load(ConfigurationSection data) {

        String skillName = data.getString("skill");
        String professionName = data.getString("profession");
        SkillsPlugin component = RaidCraft.getComponent(SkillsPlugin.class);
        try {
            Profession profession = component.getProfessionManager().getProfession(getResolver().getObject(), professionName);
            requiredSkill = (LevelableSkill) component.getSkillManager().getSkill(getResolver().getObject(), profession, skillName);
        } catch (UnknownSkillException | UnknownProfessionException e) {
            RaidCraft.LOGGER.warning(e.getMessage() + " in config of " + getResolver());
        } catch (ClassCastException e) {
            RaidCraft.LOGGER.warning("The skill level requirement " + skillName + " needs to be a levelable skill.");
        }
        super.load(data);
    }

    @Override
    public String getShortReason() {

        return "Skill " + requiredSkill.getFriendlyName() + " auf Level " + getRequiredLevel();
    }

    @Override
    public String getLongReason() {

        return ChatColor.RED +
                "Du musst erst deinen Skill " + ChatColor.AQUA + requiredSkill.getFriendlyName() +
                ChatColor.RED + " auf Level " + ChatColor.AQUA + getRequiredLevel() + ChatColor.RED + " bringen.";
    }
}
