package de.raidcraft.skills;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

/**
 * @author Silthus
 */
public final class BukkitEnvironmentManager implements Listener {

    private final SkillsPlugin plugin;

    public BukkitEnvironmentManager(SkillsPlugin plugin) {

        this.plugin = plugin;
        plugin.registerEvents(this);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onRegainHealth(EntityRegainHealthEvent event) {

        if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.REGEN
                || event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED
                || event.getRegainReason() == EntityRegainHealthEvent.RegainReason.EATING) {
            // cancel all health regain by food
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFoodGain(FoodLevelChangeEvent event) {

        // make sure we are never above 19 to allow eating
        if (event.getFoodLevel() > 19) {
            event.setFoodLevel(19);
        }
    }
}
