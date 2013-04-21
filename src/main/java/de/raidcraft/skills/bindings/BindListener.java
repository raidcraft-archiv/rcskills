package de.raidcraft.skills.bindings;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

/**
 * @author Philip
 */
public class BindListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();

        List<BoundItem> boundItems = RaidCraft.getComponent(SkillsPlugin.class).getBindManager().getBoundItems(player.getName());
        if (boundItems == null || boundItems.size() == 0) {
            return;
        }

        BoundItem boundItem = null;
        for (BoundItem item : boundItems) {
            if (item.getItem() == event.getPlayer().getItemInHand().getType()) {
                boundItem = item;
                break;
            }
        }
        if (boundItem == null) {
            return;
        }

        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            boundItem.use();
        }

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            boundItem.next();
            boundItem.getHero().sendMessage(ChatColor.DARK_GRAY + "Gewählter Skill: " + boundItem.getCurrent().getSkill().getFriendlyName());
        }
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent event) {

        RaidCraft.getComponent(SkillsPlugin.class).getBindManager().loadBoundItems(event.getPlayer());
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent event) {

        RaidCraft.getComponent(SkillsPlugin.class).getBindManager().unloadBoundItems(event.getPlayer().getName());
    }
}
