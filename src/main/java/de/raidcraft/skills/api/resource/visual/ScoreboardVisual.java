package de.raidcraft.skills.api.resource.visual;

import de.raidcraft.skills.Scoreboards;
import de.raidcraft.skills.api.resource.Resource;
import de.raidcraft.skills.api.resource.VisualResource;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Score;

/**
 * @author Silthus
 */
public class ScoreboardVisual implements VisualResource {

    @Override
    public void update(Resource resource) {

        getResourceScore(resource).setScore(resource.getCurrent());
    }

    public Score getResourceScore(Resource resource) {

        return Scoreboards.getPlayerSidebarObjective(resource.getHero()).getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + resource.getFriendlyName()));
    }
}
