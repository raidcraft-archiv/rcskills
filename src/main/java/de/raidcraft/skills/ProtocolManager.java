package de.raidcraft.skills;

import com.comphenix.packetwrapper.WrapperPlayServerChat;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.util.EntityUtil;
import mkremins.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author mdoering
 */
public class ProtocolManager extends PacketAdapter {

    // http://wiki.vg/Protocol#Chat_Message
    private static final byte CHAT_ACTION_POSITION = 0x2;
    private final SkillsPlugin plugin;

    public ProtocolManager(SkillsPlugin plugin) {

        super(plugin);
        this.plugin = plugin;
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {

            for (Player player : Bukkit.getOnlinePlayers()) {
                WrapperPlayServerChat chat = new WrapperPlayServerChat();
                Hero hero = plugin.getCharacterManager().getHero(player);
                Profession profession = hero.getHighestRankedProfession();
                FancyMessage msg = new FancyMessage((int) hero.getHealth() + "").color(EntityUtil.getHealthColor(hero.getHealth(), hero.getMaxHealth()))
                        .then("/").color(ChatColor.YELLOW)
                        .then((int) hero.getMaxHealth() + "").color(ChatColor.GREEN)
                        .then("  |  ").color(ChatColor.DARK_PURPLE)
                        .then(profession.getFriendlyName()).color(ChatColor.GOLD)
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
                chat.setMessage(WrappedChatComponent.fromJson(msg.toJSONString()));
                chat.setPosition(CHAT_ACTION_POSITION);
                chat.sendPacket(player);
            }
        }, 5L, plugin.getCommonConfig().userinterface_refresh_interval);
    }
}
