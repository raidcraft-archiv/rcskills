package de.raidcraft.skills.requirement;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.requirement.AbstractRequirement;
import de.raidcraft.api.requirement.RequirementInformation;
import de.raidcraft.api.requirement.RequirementResolver;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
@RequirementInformation("skills")
public class SkillRequirement extends AbstractRequirement<Hero> {

    private Skill requiredSkill;

    public SkillRequirement(RequirementResolver<Hero> resolver, ConfigurationSection config) {

        super(resolver, config);
    }

    @Override
    protected void load(ConfigurationSection data) {

        try {
            String skillName = data.getString("skill");
            String professionName = data.getString("profession");
            SkillsPlugin component = RaidCraft.getComponent(SkillsPlugin.class);

            Skill skill = (Skill) getResolver();
            Profession profession;
            if (professionName == null) {
                profession = skill.getProfession();
            } else {
                profession = component.getProfessionManager().getProfession(skill.getHolder(), professionName);
            }
            requiredSkill = component.getSkillManager().getSkill(skill.getHolder(), profession, skillName);
        } catch (UnknownSkillException | UnknownProfessionException e) {
            RaidCraft.LOGGER.warning(e.getMessage() + " in config of " + getResolver());
        }
    }

    @Override
    public boolean isMet(Hero object) {

        return requiredSkill != null && object.hasSkill(requiredSkill);
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
