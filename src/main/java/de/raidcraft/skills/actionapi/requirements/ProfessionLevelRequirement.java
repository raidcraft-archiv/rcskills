package de.raidcraft.skills.actionapi.requirements;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.requirement.Reasonable;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.skills.CharacterManager;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.util.ConfigUtil;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public class ProfessionLevelRequirement implements Requirement<Player>, Reasonable<Player> {


    @Override
    @Information(
            value = "profession.level",
            desc = "Checks if the given profession of the hero has the required level",
            conf = {
                    "profession: <profession>",
                    "level: <int>"
            }
    )
    public boolean test(Player player, ConfigurationSection config) {

        try {
            Hero hero = RaidCraft.getComponent(CharacterManager.class).getHero(player);
            Profession profession = hero.getProfession(config.getString("profession"));
            return config.getInt("level") < profession.getAttachedLevel().getLevel();
        } catch (UnknownSkillException | UnknownProfessionException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String getShortReason(Player player, ConfigurationSection config) {

        try {
            Hero hero = RaidCraft.getComponent(CharacterManager.class).getHero(player);
            Profession profession = hero.getProfession(config.getString("profession"));
            int requiredLevel = config.getInt("level");
            return profession.getPath().getFriendlyName() + " Spezialisierung " + profession.getFriendlyName() + " auf Level " + requiredLevel;
        } catch (UnknownSkillException | UnknownProfessionException e) {
            return "Die Spezialisierung " + config.getString("profession") + " existiert nicht in " + ConfigUtil.getFileName(config);
        }
    }

    @Override
    public String getLongReason(Player player, ConfigurationSection config) {

        String name = config.getString("profession");
        try {
            Hero hero = RaidCraft.getComponent(CharacterManager.class).getHero(player);
            Profession profession = hero.getProfession(name);
            int requiredLevel = config.getInt("level");
            String friendlyName = "???";
            if (profession == null) {
                RaidCraft.LOGGER.warning("profession is null of " + name + " in " + ConfigUtil.getFileName(config));
                return ChatColor.RED + "Config ERROR: Profession does not exist: " + name;
            } else {
                if (profession.getPath() == null) {
                    RaidCraft.LOGGER.info("path is null of " + profession.getName() + " in " + ConfigUtil.getFileName(config));
                } else {
                    friendlyName = profession.getPath().getFriendlyName();
                }
            }
            return ChatColor.RED + "Du musst erst deine " + friendlyName + " Spezialisierung " +
                    ChatColor.AQUA + profession.getFriendlyName() + ChatColor.RED + " auf " + ChatColor.AQUA + "Level "
                    + requiredLevel + ChatColor.RED + " bringen.";
        } catch (UnknownSkillException | UnknownProfessionException e) {
            RaidCraft.LOGGER.warning("profession is null of " + name + " in " + ConfigUtil.getFileName(config));
            return ChatColor.RED + "Config ERROR: Profession does not exist: " + name;
        }
    }
}
