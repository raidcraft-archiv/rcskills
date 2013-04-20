package de.raidcraft.skills.api.resource.visual;

import de.raidcraft.skills.Scoreboards;
import de.raidcraft.skills.api.resource.Resource;
import de.raidcraft.skills.api.resource.VisualResource;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

/**
 * @author Silthus
 */
public class ScoreboardVisual implements VisualResource {

    public static final String OBJECTIVE_BASE_NAME = "rcsr";
    private static final String DISPLAY_NAME = "Resourcen";

    @Override
    public void update(Resource resource) {

        getResourceScore(resource).setScore(resource.getCurrent());
    }

    public Objective getResourceObjective(Resource resource) {

        Scoreboard scoreboard = Scoreboards.getScoreboard(resource.getHero().getPlayer());

        String objectiveName = OBJECTIVE_BASE_NAME + resource.getHero().getId();
        Objective objective;
        if (scoreboard.getObjective(objectiveName) == null) {
            objective = scoreboard.registerNewObjective(objectiveName, "dummy");
            objective.setDisplayName(DISPLAY_NAME);
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        } else {
            objective = scoreboard.getObjective(objectiveName);
        }
        return objective;
    }

    public Score getResourceScore(Resource resource) {

        return getResourceObjective(resource).getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + resource.getFriendlyName()));
    }
}
