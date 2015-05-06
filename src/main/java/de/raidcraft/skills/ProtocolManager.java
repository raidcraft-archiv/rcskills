package de.raidcraft.skills;

import com.comphenix.packetwrapper.WrapperPlayServerChat;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.util.EntityUtil;
import mkremins.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author mdoering
 */
public class ProtocolManager {

    // http://wiki.vg/Protocol#Chat_Message
    private static final int CHAT_ACTION_POSITION = 2;
    private final SkillsPlugin plugin;

    public ProtocolManager(SkillsPlugin plugin) {

        this.plugin = plugin;
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {

            for (Player player : Bukkit.getOnlinePlayers()) {
                WrapperPlayServerChat chat = new WrapperPlayServerChat();
                Hero hero = plugin.getCharacterManager().getHero(player);
                FancyMessage msg = new FancyMessage((int) hero.getHealth() + "").color(EntityUtil.getHealthColor(hero.getHealth(), hero.getMaxHealth()))
                        .then("/").color(ChatColor.YELLOW)
                        .then((int) hero.getMaxHealth() + "").color(ChatColor.GREEN)
                        .then("  |  ").color(ChatColor.DARK_PURPLE);
                msg = renderProfessions(hero, msg);
                chat.setMessage(WrappedChatComponent.fromText(EntityUtil.getHealthColor(hero.getHealth(), hero.getMaxHealth()) + "" + (int) hero.getHealth()
                        + ChatColor.YELLOW + "/" + ChatColor.GREEN + (int) hero.getMaxHealth() + ChatColor.DARK_PURPLE + "  |  "));
                chat.setPosition((byte) CHAT_ACTION_POSITION);
                chat.sendPacket(player);
            }
        }, 5L, plugin.getCommonConfig().userinterface_refresh_interval);
    }

    private FancyMessage renderProfessions(Hero hero, FancyMessage message) {

        List<Profession> professions = hero.getProfessions().stream().filter(Profession::isActive).collect(Collectors.toList());
        Profession primary = null;
        Profession secundary = null;
        for (Profession profession : professions) {
            if (profession.getPath().getName().equalsIgnoreCase(plugin.getCommonConfig().primary_path)) {
                primary = profession;
            } else if (profession.getPath().getName().equalsIgnoreCase(plugin.getCommonConfig().secundary_path)) {
                secundary = profession;
            }
        }
        if (primary != null) {
            message = renderProfession(primary, message.then("  |  ").color(ChatColor.DARK_PURPLE));
        }
        if (secundary != null) {
            message = renderProfession(secundary, message.then("  |  ").color(ChatColor.DARK_PURPLE));
        }
        return message;
    }

    private FancyMessage renderProfession(Profession profession, FancyMessage message) {

        return message.then(profession.getFriendlyName()).color(ChatColor.GOLD)
                .then(" - ").color(ChatColor.DARK_PURPLE)
                .then("Level: ").color(ChatColor.YELLOW)
                .then(profession.getAttachedLevel().getLevel() + "").color(ChatColor.AQUA)
                .then("/").color(ChatColor.YELLOW)
                .then(profession.getAttachedLevel().getMaxLevel() + "").color(ChatColor.AQUA)
                .then(" - ").color(ChatColor.DARK_PURPLE)
                .then("EXP: ").color(ChatColor.YELLOW)
                .then(profession.getAttachedLevel().getExp() + "").color(ChatColor.AQUA)
                .then("/").color(ChatColor.YELLOW)
                .then(profession.getAttachedLevel().getMaxExp() + "").color(ChatColor.AQUA);
    }
}
