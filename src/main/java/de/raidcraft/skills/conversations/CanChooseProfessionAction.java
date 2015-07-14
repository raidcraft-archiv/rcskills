package de.raidcraft.skills.conversations;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public class CanChooseProfessionAction implements Requirement<Player> {

    @Override
    @Information(
            value = "profession.choose",
            desc = "Checks if the player can choose the given profession.",
            conf = {
                    "profession"
            },
            aliases = {"CAN_CHOOSE_PROFESSION"}
    )
    public boolean test(Player player, ConfigurationSection config) {

        SkillsPlugin plugin = RaidCraft.getComponent(SkillsPlugin.class);

        Hero hero = plugin.getCharacterManager().getHero(player);
        if (hero == null) return false;
        Profession profession;

        try {
            profession = plugin.getProfessionManager().getProfession(hero, config.getString("profession"));
            return profession.isMeetingAllRequirements(hero.getPlayer());
        } catch (UnknownSkillException | UnknownProfessionException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
