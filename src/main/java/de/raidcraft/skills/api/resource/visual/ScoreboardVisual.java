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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public class ScoreboardVisual implements VisualResource {

    public static final String OBJECTIVE_BASE_NAME = "resources";
    private static final String DISPLAY_NAME = "Resourcen";

    private final Map<String, Scoreboard> scoreboards = new HashMap<>();

    @Override
    public void update(Resource resource) {

        Player player = resource.getHero().getPlayer();
        String playerName = player.getName().toLowerCase();

        Scoreboard scoreboard;
        Objective objective;
        if (scoreboards.containsKey(playerName)) {
            scoreboard = scoreboards.get(playerName);
            objective = scoreboard.getObjective(OBJECTIVE_BASE_NAME + "_" + playerName);
        } else {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            scoreboards.put(playerName, scoreboard);
            objective = scoreboard.registerNewObjective(OBJECTIVE_BASE_NAME + "_" + playerName, "dummy");
            objective.setDisplayName("Resourcen");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        // now lets actually set the resource
        // we fetch a fake offline player here
        Score score = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + resource.getFriendlyName()));
        score.setScore(resource.getCurrent());

        player.setScoreboard(scoreboard);
    }

    public void disable(Resource resource) {

        String playerName = resource.getHero().getName().toLowerCase();
        if (scoreboards.containsKey(playerName)) {
            Scoreboard scoreboard = scoreboards.get(playerName);
            Objective objective = scoreboard.getObjective(OBJECTIVE_BASE_NAME + "_" + playerName);
            objective.unregister();
        }
    }
}
