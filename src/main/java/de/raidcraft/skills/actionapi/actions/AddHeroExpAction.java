package de.raidcraft.skills.actionapi.actions;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.hero.Hero;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author mdoering
 */
public class AddHeroExpAction implements Action<Player> {

    @Override
    @Information(
            value = "hero.addxp",
            desc = "Checks if the player is at or in a radius of the given location.",
            conf = {
                    "world: [current]",
                    "x",
                    "y",
                    "z",
                    "radius: [0]"
            }
    )
    public void accept(Player player, ConfigurationSection config) {

        SkillsPlugin plugin = RaidCraft.getComponent(SkillsPlugin.class);
        Hero hero = plugin.getCharacterManager().getHero(player);
        if (hero == null) return;
	    int exp = config.getInt("exp");
        hero.getExpPool().addExp(exp);
	    hero.sendMessage("Deinem EXP Pool wurden "+exp+" EXP hinzugefügt.");
    }
}
