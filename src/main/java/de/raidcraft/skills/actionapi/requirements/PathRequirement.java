package de.raidcraft.skills.actionapi.requirements;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.skills.CharacterManager;
import de.raidcraft.skills.api.hero.Hero;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class PathRequirement implements Requirement<Player> {

    @Information(
            value = "hero.path",
            desc = "Checks if the hero has chosen the given path.",
            conf = {
                    "path: id of the path"
            }
    )
    @Override
    public boolean test(Player player, ConfigurationSection config) {

        Hero hero = RaidCraft.getComponent(CharacterManager.class).getHero(player);
        if (hero == null) return false;
        return hero.hasPath(config.getString("path"));
    }
}
