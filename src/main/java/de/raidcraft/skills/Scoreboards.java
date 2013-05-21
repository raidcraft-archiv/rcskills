package de.raidcraft.skills;

import de.raidcraft.skills.api.ui.BukkitUserInterface;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public final class Scoreboards {

    private static final Map<String, Scoreboard> scoreboards = new HashMap<>();

    public static Scoreboard getScoreboard(Player player) {

        String playerName = player.getName().toLowerCase();
        if (scoreboards.containsKey(playerName)) {
            return scoreboards.get(playerName);
        }
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        player.setScoreboard(scoreboard);
        scoreboards.put(playerName, scoreboard);
        return scoreboard;
    }

    public static Scoreboard removeScoreboard(Player player) {

        if (player == null) {
            return null;
        }
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        return scoreboards.remove(player.getName().toLowerCase());
    }

    public static void updateHealthDisplays() {

        for (Scoreboard scoreboard : scoreboards.values()) {
            for (Objective objective : scoreboard.getObjectives()) {
                if (objective.getName().startsWith(BukkitUserInterface.HEALTH_OBJECTIVE)) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        objective.getScore(player).setScore(player.getHealth());
                    }
                }
            }
        }
    }
}
