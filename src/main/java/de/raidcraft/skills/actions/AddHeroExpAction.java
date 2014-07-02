package de.raidcraft.skills.actions;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.hero.Hero;
import org.bukkit.entity.Player;

/**
 * @author mdoering
 */
public class AddHeroExpAction implements Action<Player> {

    @Override
    public void accept(Player player) {

        SkillsPlugin plugin = RaidCraft.getComponent(SkillsPlugin.class);
        Hero hero = plugin.getCharacterManager().getHero(player);
        hero.getExpPool().addExp(getConfig().getInt("exp"));
    }
}