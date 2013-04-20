package de.raidcraft.skills.api.ui;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.GamePhase;
import com.comphenix.protocol.injector.PlayerLoggedOutException;
import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Silthus
 */
public class BukkitUserInterface implements UserInterface {

    private static final String HEALTH_OBJECTIVE = "hero_health";

    private final ProtocolManager protocolManager;
    private final Hero hero;
    private final Player player;

    public BukkitUserInterface(final Hero hero) {

        this.hero = hero;
        this.player = hero.getPlayer();

        getScoreboardHealthObjective().setDisplayName("/ " + hero.getMaxHealth());

        // lets intercept server packets that tell the player the experience
        protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new PacketAdapter(
                RaidCraft.getComponent(SkillsPlugin.class),
                ConnectionSide.SERVER_SIDE,
                ListenerPriority.HIGHEST,
                GamePhase.PLAYING,
                Packets.Server.SET_EXPERIENCE) {
            @Override
            public void onPacketSending(PacketEvent event) {

                modifyExperiencePacket(event.getPacket());
            }
        });
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
        Objective objective = getScoreboardHealthObjective();
        objective.setDisplayName("HP");
        objective.getScore(player).setScore(getHero().getHealth());

        // make sure the food level is never at 20 to allow eating
        if (player.getFoodLevel() > 19) {
            player.setFoodLevel(19);
        }

        // lets also send an exp update packet to the client
        try {
            // we only need to create a default packet - it will be modified in our listener
            protocolManager.sendServerPacket(getHero().getPlayer(), protocolManager.createPacket(Packets.Server.SET_EXPERIENCE));
        } catch (InvocationTargetException | PlayerLoggedOutException ignored) {
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
        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard == null) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            player.setScoreboard(scoreboard);
        }
        Objective objective = scoreboard.getObjective(HEALTH_OBJECTIVE);
        if (objective == null) {
            objective = scoreboard.registerNewObjective(HEALTH_OBJECTIVE, "dummy");
            objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        }
        return objective;
    }
}
