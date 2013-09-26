package de.raidcraft.skills.api.ui;

import de.raidcraft.skills.Scoreboards;
import de.raidcraft.skills.api.hero.Hero;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

/**
 * @author Silthus
 */
public class BukkitUserInterface implements UserInterface {

    public static final String HEALTH_OBJECTIVE = "rcshp";

    private final Hero hero;

    public BukkitUserInterface(final Hero hero) {

        this.hero = hero;
    }

    @Override
    public Hero getHero() {

        return hero;
    }

    private void updateHealthDisplay() {

        // update what others see
        Objective objective = getScoreboardHealthObjective();
        for (Player player : Bukkit.getOnlinePlayers()) {
            objective.getScore(player).setScore((int) player.getHealth());
        }
        // update what the player sees
        getHero().getPlayer().setLevel(getHero().getHealth());
    }

    @Override
    public void refresh() {

        if (!hero.isOnline()
                || hero.getPlayer().isDead()
                || hero.getHealth() < 1) {
            return;
        }

        // lets update the scoreboard
        updateHealthDisplay();
        // make sure the food level is never at 20 to allow eating
        if (hero.getPlayer().getFoodLevel() > 19) {
            hero.getPlayer().setFoodLevel(19);
        }
    }

    private Objective getScoreboardHealthObjective() {

        // lets also set the scoreboard to display the health of this player to all online players
        Scoreboard scoreboard = Scoreboards.getScoreboard(hero);

        Objective objective = scoreboard.getObjective(HEALTH_OBJECTIVE);
        if (objective == null) {
            objective = scoreboard.registerNewObjective(HEALTH_OBJECTIVE, "health");
            objective.setDisplayName(ChatColor.RED + "❤");
            objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        }
        return objective;
    }
}
