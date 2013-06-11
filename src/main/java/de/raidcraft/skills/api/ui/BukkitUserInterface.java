package de.raidcraft.skills.api.ui;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.injector.PlayerLoggedOutException;
import de.raidcraft.RaidCraft;
import de.raidcraft.skills.CharacterManager;
import de.raidcraft.skills.Scoreboards;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.resource.Resource;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Silthus
 */
public class BukkitUserInterface implements UserInterface {

    public static final String HEALTH_OBJECTIVE = "hp";

    private final Hero hero;

    public BukkitUserInterface(final Hero hero) {

        this.hero = hero;
    }

    @Override
    public Hero getHero() {

        return hero;
    }

    private void updateSidebar() {

        Objective objective = Scoreboards.getPlayerSidebarObjective(getHero());
        objective.getScore(Bukkit.getOfflinePlayer(ChatColor.DARK_GRAY + "Rüstung")).setScore(getHero().getTotalArmorValue());
        objective.getScore(Bukkit.getOfflinePlayer(ChatColor.RED + "Leben")).setScore(getHero().getHealth());
        // update all resource displays
        for (Resource resource : getHero().getResources()) {
            if (resource.getName().equals("health")) {
                continue;
            }
            if (resource.isEnabled() && resource.getProfession().isActive()) {
                objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + resource.getFriendlyName())).setScore(resource.getCurrent());
            }
        }
    }

    private void updateHealthDisplay() {

        getScoreboardHealthObjective();
        Scoreboards.updateHealthDisplays();
    }

    private void updateExperienceDisplay() {

        CharacterManager characterManager = RaidCraft.getComponent(SkillsPlugin.class).getCharacterManager();
        if (characterManager.isPausingPlayerExpUpdate(getHero().getPlayer())) {
            return;
        }
        if (!characterManager.isPlayerCached(getHero().getName())) {
            return;
        }
        try {
            hero.getPlayer().setLevel(0);
            hero.getPlayer().setExp(0.0F);
            hero.getPlayer().setTotalExperience(0);
            PacketContainer packet = new PacketContainer(Packets.Server.SET_EXPERIENCE);
            modifyExperiencePacket(packet);
            ProtocolLibrary.getProtocolManager().sendServerPacket(hero.getPlayer(), packet);
        } catch (InvocationTargetException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
        } catch (PlayerLoggedOutException ignored) {
        }
    }

    @Override
    public void refresh() {

        if (!hero.isOnline()
                || hero.getPlayer().isDead()
                || hero.getHealth() < 1) {
            return;
        }

        updateSidebar();
        updateExperienceDisplay();
        // lets update the scoreboard
        updateHealthDisplay();
        // make sure the food level is never at 20 to allow eating
        if (hero.getPlayer().getFoodLevel() > 19) {
            hero.getPlayer().setFoodLevel(19);
        }
    }

    public void modifyExperiencePacket(PacketContainer packet) {

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
        packet.getIntegers().write(0, 0);
    }

    private Objective getScoreboardHealthObjective() {

        // lets also set the scoreboard to display the health of this player to all online players
        Scoreboard scoreboard = Scoreboards.getScoreboard(hero);

        Objective objective = scoreboard.getObjective(HEALTH_OBJECTIVE + hero.getId());
        if (objective == null) {
            objective = scoreboard.registerNewObjective(HEALTH_OBJECTIVE + hero.getId(), "dummy");
            objective.setDisplayName("❤");
            objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        }
        return objective;
    }
}
