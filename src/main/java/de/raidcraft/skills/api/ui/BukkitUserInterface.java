package de.raidcraft.skills.api.ui;

import com.comphenix.protocol.events.PacketContainer;
import de.raidcraft.skills.Scoreboards;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

/**
 * @author Silthus
 */
public class BukkitUserInterface implements UserInterface {

    private static final String HEALTH_OBJECTIVE = "rcshealth";

    private final Hero hero;
    private final Player player;

    public BukkitUserInterface(final Hero hero) {

        this.hero = hero;
        this.player = hero.getPlayer();

        // getHealthScore().setScore(hero.getHealth());
    }

    @Override
    public Hero getHero() {

        return hero;
    }

    @Override
    public void refresh() {

        if (player == null
                || !player.isOnline()
                || player.getGameMode() == GameMode.CREATIVE
                || player.isDead()
                || player.getHealth() < 1) {
            return;
        }

        // lets update the scoreboard
        // getHealthScore().setScore(getHero().getHealth());

        // make sure the food level is never at 20 to allow eating
        if (player.getFoodLevel() > 19) {
            player.setFoodLevel(19);
        }
    }

    private void modifyExperiencePacket(PacketContainer packet) {

        Profession prof = hero.getSelectedProfession();
        float exp;
        int level;
        if (prof != null) {
            // setExp() - This is a percentage value. 0 is "no progress" and 1 is "next level".
            exp = ((float) prof.getAttachedLevel().getExp()) / ((float) prof.getAttachedLevel().getMaxExp());
            level = prof.getAttachedLevel().getLevel();
        } else {
            // lets set the level to 0
            exp = 0.0F;
            level = 0;
        }
        // lets modify the actual paket
        packet.getFloat().write(0, exp);
        packet.getIntegers().write(1, level);
    }

    private Objective getScoreboardHealthObjective() {

        // lets also set the scoreboard to display the health of this player to all online players
        Scoreboard scoreboard = Scoreboards.getScoreboard(player);

        Objective objective = scoreboard.getObjective(HEALTH_OBJECTIVE);
        if (objective == null) {
            objective = scoreboard.registerNewObjective(HEALTH_OBJECTIVE, "dummy");
            objective.setDisplayName("❤");
            objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        }
        return objective;
    }

    public Score getHealthScore() {

        return getScoreboardHealthObjective().getScore(player);
    }
}
