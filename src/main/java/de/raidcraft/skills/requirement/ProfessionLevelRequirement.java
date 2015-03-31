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
    protected void load(ConfigurationSection data) {

        try {

            profession = RaidCraft.getComponent(SkillsPlugin.class).getProfessionManager().getProfession(getResolver().getObject(), data.getString("profession"));
            RaidCraft.getComponent(SkillsPlugin.class).info("load ProfessionLevelRequirement: " + profession, "profession.requirement");
        } catch (UnknownSkillException | UnknownProfessionException e) {
            RaidCraft.LOGGER.warning(e.getMessage() + " in config of " + getResolver());
        }
        super.load(data);
    }

    @Override
    protected Levelable getLevelable() {

        return profession;
    }

    @Override
    public String getShortReason() {

        return profession.getPath().getFriendlyName() + " Spezialisierung " + profession.getFriendlyName() + " auf Level " + getRequiredLevel();
    }

    @Override
    public String getLongReason() {

        String friendlyName = "???";
        if (profession == null) {
            RaidCraft.LOGGER.info("profession is null of " + getName());
            return ChatColor.RED + "Config ERROR: Profession does not exist: " + getName();
        } else {
            if (profession.getPath() == null) {
                RaidCraft.LOGGER.info("path is null of " + profession.getName());
            } else {
                friendlyName = profession.getPath().getFriendlyName();
            }
        }
        return ChatColor.RED + "Du musst erst deine " + friendlyName + " Spezialisierung " +
                ChatColor.AQUA + profession.getFriendlyName() + ChatColor.RED + " auf " + ChatColor.AQUA + "Level "
                + getRequiredLevel() + ChatColor.RED + " bringen.";
    }
}
