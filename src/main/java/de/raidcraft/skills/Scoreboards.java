package de.raidcraft.skills;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.util.HeroUtil;
import de.raidcraft.util.CaseInsensitiveMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Map;

/**
 * @author Silthus
 */
public final class Scoreboards {

    private static final String HEALTH_OBJECTIVE = "rcshp";
    private static final String OBJECTIVE_SIDE_BASE_NAME = "side";
    private static final String SIDE_DISPLAY_NAME = "---- %pvp% ----";
    private static final Map<String, Scoreboard> scoreboards = new CaseInsensitiveMap<>();

    public static void removeScoreboard(Player player) {

        if (player == null) {
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

    public static void updateHealthDisplays() {

        for (Scoreboard scoreboard : scoreboards.values()) {
            Objective objective = scoreboard.getObjective(HEALTH_OBJECTIVE);
            if (objective == null) {
                objective = scoreboard.registerNewObjective(HEALTH_OBJECTIVE, "health");
                objective.setDisplayName(ChatColor.DARK_RED + "❤");
                objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
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
        if (hero.isOnline() && hero.getEntity().hasMetadata("GHOST")) {
            objective.setDisplayName(ChatColor.DARK_GRAY + ChatColor.stripColor(SIDE_DISPLAY_NAME.replace("%pvp%", "Geist")));
        } else {
            objective.setDisplayName(HeroUtil.getPvPColor(hero, null) + ChatColor.stripColor(SIDE_DISPLAY_NAME.replace("%pvp%", HeroUtil.getPvPTag(hero))));
        }
        return objective;
    }

    public static Scoreboard getScoreboard(Hero hero) {

        String playerName = hero.getName();
        Scoreboard scoreboard;
        if (scoreboards.containsKey(playerName)) {
            scoreboard = scoreboards.get(playerName);
        } else {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        }
        if (!hero.isOnline()) {
            hero.updateEntity(Bukkit.getPlayer(hero.getPlayer().getUniqueId()));
            return scoreboard;
        }
        hero.getPlayer().setScoreboard(scoreboard);
        scoreboards.put(playerName, scoreboard);
        return scoreboard;
    }

    public static Team updatePlayerTeam(Hero hero) {

        Scoreboard scoreboard = getScoreboard(hero);
        String teamId = (hero.hashCode() + "");
        if (teamId.length() > 15) teamId = teamId.substring(0, 15);
        Team team = scoreboard.getTeam(teamId);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamId);
            Profession profession = hero.getHighestRankedProfession();
            ChatColor color = profession.getProperties().getColor();
            String friendlyName = color + profession.getFriendlyName() + " " + ChatColor.GOLD;
            if (friendlyName.length() > 15) friendlyName = friendlyName.substring(0, 15);
            team.setPrefix(friendlyName);
            String level = color + " [" + ChatColor.AQUA + profession.getAttachedLevel().getLevel() + color + "]";
            if (level.length() > 15) level = level.substring(0, 15);
            team.setSuffix(level);
            team.setDisplayName(teamId);
            team.setAllowFriendlyFire(true);
            team.setCanSeeFriendlyInvisibles(true);
            team.setNameTagVisibility(NameTagVisibility.ALWAYS);
            team.addPlayer(Bukkit.getOfflinePlayer(hero.getName()));
            updateTeams();
        }
        return team;
    }

    public static void updateTeams() {

        for (Scoreboard scoreboard : scoreboards.values()) {
            for (Team team : scoreboard.getTeams()) {
                for (Scoreboard score : scoreboards.values()) {
                    Team scoreboardTeam = score.getTeam(team.getName());
                    if (scoreboardTeam == null) {
                        Team newTeam = score.registerNewTeam(team.getName());
                        newTeam.setPrefix(team.getPrefix());
                        newTeam.setSuffix(team.getSuffix());
                        newTeam.setDisplayName(team.getDisplayName());
                    }
                }
            }
        }
    }
}
