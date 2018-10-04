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
            desc = "Gives the player the given amount of exp.",
            conf = {
                    "exp: exp amount"
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
