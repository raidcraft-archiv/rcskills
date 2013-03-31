package de.raidcraft.skills.requirement;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.requirement.AbstractRequirement;
import de.raidcraft.api.requirement.RequirementInformation;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
@RequirementInformation("skills")
public class SkillRequirement extends AbstractRequirement<SkillRequirementResolver> {

    private Skill requiredSkill;

    public SkillRequirement(SkillRequirementResolver type, ConfigurationSection config) {

        super(type, config);
    }

    @Override
    protected void load(ConfigurationSection data) {

        try {
            String skillName = data.getString("skill");
            String professionName = data.getString("profession");
            SkillsPlugin component = RaidCraft.getComponent(SkillsPlugin.class);

            Profession profession;
            if (professionName == null && getResolver() instanceof Skill) {
                profession = ((Skill) getResolver()).getProfession();
            } else {
                profession = component.getProfessionManager().getProfession(getResolver().getHero(), professionName);
            }
            requiredSkill = component.getSkillManager().getSkill(getResolver().getHero(), profession, skillName);
        } catch (UnknownSkillException | UnknownProfessionException e) {
            RaidCraft.LOGGER.warning(e.getMessage() + " in config of " + getResolver());
        }
    }

    @Override
    public boolean isMet() {

        if (requiredSkill == null) return false;
        return getResolver().getHero().hasSkill(requiredSkill) && requiredSkill.isUnlocked();
    }

    @Override
    public String getLongReason() {

        return ChatColor.RED + "Du benötigst den Skill " + ChatColor.AQUA + requiredSkill + ChatColor.RED + ".";
    }

    @Override
    public String getShortReason() {

        return "Freigeschalteter Skill: " + requiredSkill;
    }
}
