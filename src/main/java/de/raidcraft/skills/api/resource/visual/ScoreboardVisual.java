package de.raidcraft.skills.api.resource.visual;

import de.raidcraft.skills.api.resource.Resource;
import de.raidcraft.skills.api.resource.VisualResource;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

/**
 * @author Silthus
 */
public class ScoreboardVisual implements VisualResource {

    public static final String SCOREBOARD_NAME = "resources";
    private static final String DISPLAY_NAME = "Resourcen";

    @Override
    public void update(Resource resource) {

        Player player = resource.getHero().getPlayer();

        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard == null) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        }

        Objective objective = scoreboard.getObjective(SCOREBOARD_NAME);
        if (objective == null) {
            objective = scoreboard.registerNewObjective(SCOREBOARD_NAME, "dummy");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            objective.setDisplayName(DISPLAY_NAME);
        }

        // now lets actually set the resource
        // we fetch a fake offline player here
        Score score = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + resource.getFriendlyName()));
        score.setScore(resource.getCurrent());

        player.setScoreboard(scoreboard);
    }
}
