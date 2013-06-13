package de.raidcraft.skills;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.ui.BukkitUserInterface;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public final class Scoreboards {

    private static final String OBJECTIVE_SIDE_BASE_NAME = "side";
    private static final String SIDE_DISPLAY_NAME = "---- %pvp% ----";
    private static final String TEAM_NAME = "raidcraft";
    private static final Map<String, Scoreboard> scoreboards = new HashMap<>();

    public static Scoreboard getScoreboard(Hero hero) {

        String playerName = hero.getName().toLowerCase();
        if (scoreboards.containsKey(playerName)) {
            return scoreboards.get(playerName);
        }
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        if (!hero.isOnline()) {
            hero.updateEntity(Bukkit.getPlayer(playerName));
        }
        hero.getPlayer().setScoreboard(scoreboard);
        scoreboards.put(playerName, scoreboard);
        return scoreboard;
    }

    public static void removeScoreboard(Player player) {

        if (player == null || !player.isOnline()) {
            return;
        }
        Scoreboard scoreboard = scoreboards.remove(player.getName().toLowerCase());
        if (scoreboard != null) {
            for (Objective objective : scoreboard.getObjectives()) {
                objective.unregister();
            }
            for (Team team : scoreboard.getTeams()) {
                team.unregister();
            }
            for (DisplaySlot slot : DisplaySlot.values()) {
                scoreboard.clearSlot(slot);
            }
        }
    }

    public static void updateTeams() {

        for (Scoreboard scoreboard : scoreboards.values()) {
            Team team = scoreboard.getTeam(TEAM_NAME);
            if (team == null) {
                team = scoreboard.registerNewTeam(TEAM_NAME);
                team.setAllowFriendlyFire(true);
                team.setCanSeeFriendlyInvisibles(true);
            }
            for (Player player : Bukkit.getOnlinePlayers()) {
                team.addPlayer(player);
            }
        }
    }

    public static void updateHealthDisplays() {

        for (Scoreboard scoreboard : scoreboards.values()) {
            for (Objective objective : scoreboard.getObjectives()) {
                if (objective.getName().startsWith(BukkitUserInterface.HEALTH_OBJECTIVE)) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.getHealth() > 0) {
                            objective.getScore(player).setScore(player.getHealth());
                        }
                    }
                }
            }
        }
    }

    public static Objective getPlayerSidebarObjective(Hero hero) {

        Scoreboard scoreboard = Scoreboards.getScoreboard(hero);

        String objectiveName = OBJECTIVE_SIDE_BASE_NAME + hero.getId();
        Objective objective;
        if (scoreboard.getObjective(objectiveName) == null) {
            objective = scoreboard.registerNewObjective(objectiveName, "dummy");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        } else {
            objective = scoreboard.getObjective(objectiveName);
        }
        if (hero.getEntity().hasMetadata("GHOST")) {
            objective.setDisplayName(ChatColor.DARK_GRAY + SIDE_DISPLAY_NAME.replace("%pvp%", "Geist"));
        } else {
            ChatColor color;
            if (hero.getParty().getHeroes().size() > 1) {
                if (hero.isPvPEnabled()) {
                    color = ChatColor.DARK_GREEN;
                } else {
                    color = ChatColor.GREEN;
                }
            } else {
                if (hero.isPvPEnabled()) {
                    color = ChatColor.DARK_RED;
                } else {
                    color = ChatColor.AQUA;
                }
            }
            objective.setDisplayName(color + SIDE_DISPLAY_NAME.replace("%pvp%", "PvP: " + (hero.isPvPEnabled() ? "an" : "aus")));
        }
        return objective;
    }
}
