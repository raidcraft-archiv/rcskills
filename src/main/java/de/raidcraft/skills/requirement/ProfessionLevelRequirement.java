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
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
@RequirementInformation("profession-level")
public class ProfessionLevelRequirement extends LevelRequirement {

    private Profession profession;

    public ProfessionLevelRequirement(RequirementResolver<Hero> resolver, ConfigurationSection config) {

        super(resolver, config);
    }

    @Override
    protected Levelable getLevelable() {

        return profession;
    }

    @Override
    protected void load(ConfigurationSection data) {

        try {
            profession = RaidCraft.getComponent(SkillsPlugin.class).getProfessionManager().getProfession((Profession)getResolver(), data.getString("profession"));
        } catch (UnknownSkillException | UnknownProfessionException e) {
            RaidCraft.LOGGER.warning(e.getMessage() + " in config of " + getResolver());
        }
        super.load(data);
    }

    @Override
    public String getLongReason() {

        return ChatColor.RED + "Du musst erst deine " + profession.getPath().getFriendlyName() + " Spezialisierung " +
                ChatColor.AQUA + profession.getFriendlyName() + ChatColor.RED + " auf " + ChatColor.AQUA + "Level "
                + getRequiredLevel() + ChatColor.RED + " bringen.";
    }

    @Override
    public String getShortReason() {

        return profession.getPath().getFriendlyName() + " Spezialisierung " + profession.getFriendlyName() + " auf Level " + getRequiredLevel();
    }
}
