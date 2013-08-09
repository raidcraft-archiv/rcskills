package de.raidcraft.skills;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * @author Silthus
 */
public final class BukkitEnvironmentManager implements Listener {

    private final SkillsPlugin plugin;

    public BukkitEnvironmentManager(SkillsPlugin plugin) {

        this.plugin = plugin;
        plugin.registerEvents(this);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {

        if (plugin.getCommonConfig().health_scale > 0) {
            // lets set the player health scale
            event.getPlayer().setHealthScale(plugin.getCommonConfig().health_scale);
        } else {
            event.getPlayer().setHealthScaled(false);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onFoodGain(FoodLevelChangeEvent event) {

        // make sure we are never above 19 to allow eating
        if (event.getFoodLevel() > 19) {
            event.setFoodLevel(19);
        }
    }
}