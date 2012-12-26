package de.raidcraft.skills;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

/**
 * @author Silthus
 */
public final class StaminaManager implements Listener {

    private final SkillsPlugin plugin;

    public StaminaManager(SkillsPlugin plugin) {

        this.plugin = plugin;
        plugin.registerEvents(this);
    }

    public void reload() {

        // nothing to do here
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onRegainHealth(EntityRegainHealthEvent event) {

        if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.REGEN
                || event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
            // cancel all health regain by food
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {

        //TODO: set saturation
        // cancel all food level change, since thats our stamina bar
        event.setCancelled(true);
    }
}
