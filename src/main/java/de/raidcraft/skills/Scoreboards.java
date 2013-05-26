package de.raidcraft.skills;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.ui.BukkitUserInterface;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public final class Scoreboards {

    private static final String OBJECTIVE_SIDE_BASE_NAME = "side";
    private static final String OBJECTIVE_LIST_BASE_NAME = "list";
    private static final String LIST_DISPLAY_NAME = "Stats";
    private static final String SIDE_DISPLAY_NAME = "Charakterübersicht";
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

    public static void removeScoreboard(Player player) {

        if (player == null) {
            return;
        }
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        Scoreboard scoreboard = scoreboards.remove(player.getName().toLowerCase());
        if (scoreboard != null) {
            for (Objective objective : scoreboard.getObjectives()) {
                objective.unregister();
            }
            for (DisplaySlot slot : DisplaySlot.values()) {
                scoreboard.clearSlot(slot);
            }
        }
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

    public static Objective getPlayerTabListObjective(Hero hero) {

        Scoreboard scoreboard = Scoreboards.getScoreboard(hero.getPlayer());

        String objectiveName = OBJECTIVE_LIST_BASE_NAME + hero.getId();
        Objective objective;
        if (scoreboard.getObjective(objectiveName) == null) {
            objective = scoreboard.registerNewObjective(objectiveName, "dummy");
            objective.setDisplayName(LIST_DISPLAY_NAME);
            objective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        } else {
            objective = scoreboard.getObjective(objectiveName);
        }
        return objective;
    }

    public static Objective getPlayerSidebarObjective(Hero hero) {

        Scoreboard scoreboard = Scoreboards.getScoreboard(hero.getPlayer());

        String objectiveName = OBJECTIVE_SIDE_BASE_NAME + hero.getId();
        Objective objective;
        if (scoreboard.getObjective(objectiveName) == null) {
            objective = scoreboard.registerNewObjective(objectiveName, "dummy");
            objective.setDisplayName(SIDE_DISPLAY_NAME);
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        } else {
            objective = scoreboard.getObjective(objectiveName);
        }
        return objective;
    }
}
