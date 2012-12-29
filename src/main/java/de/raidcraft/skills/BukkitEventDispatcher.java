package de.raidcraft.skills;

import de.raidcraft.skills.api.trigger.TriggerManager;
import de.raidcraft.skills.trigger.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

/**
 * @author Silthus
 */
public class BukkitEventDispatcher implements Listener {

    private final SkillsPlugin plugin;

    public BukkitEventDispatcher(SkillsPlugin plugin) {

        this.plugin = plugin;
        plugin.registerEvents(this);
        // TODO: add prios to events
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {

        TriggerManager.callTrigger(
                new BlockBreakTrigger(plugin.getCharacterManager().getHero(event.getPlayer()), event)
        );
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {

        TriggerManager.callTrigger(
                new BlockPlaceTrigger(plugin.getCharacterManager().getHero(event.getPlayer()), event)
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {

        TriggerManager.callTrigger(
                new PlayerInteractTrigger(plugin.getCharacterManager().getHero(event.getPlayer()), event)
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {

        TriggerManager.callTrigger(
                new InventoryCloseTrigger(plugin.getCharacterManager().getHero((Player) event.getPlayer()), event)
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemHeld(PlayerItemHeldEvent event) {

        TriggerManager.callTrigger(
                new ItemHeldTrigger(plugin.getCharacterManager().getHero(event.getPlayer()), event)
        );
    }
}
