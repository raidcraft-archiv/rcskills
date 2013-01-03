package de.raidcraft.skills;

import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.skills.api.trigger.TriggerManager;
import de.raidcraft.skills.trigger.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public class BukkitEventDispatcher implements Listener {

    private final SkillsPlugin plugin;
    private final Map<Block, String> brewingPlayers = new HashMap<>();

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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onInventoryClose(InventoryCloseEvent event) {

        TriggerManager.callTrigger(
                new InventoryCloseTrigger(plugin.getCharacterManager().getHero((Player) event.getPlayer()), event)
        );
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onItemHeld(PlayerItemHeldEvent event) {

        TriggerManager.callTrigger(
                new ItemHeldTrigger(plugin.getCharacterManager().getHero(event.getPlayer()), event)
        );
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent event) {

        TriggerManager.callTrigger(
                new InventoryClickTrigger(plugin.getCharacterManager().getHero((Player) event.getWhoClicked()), event)
        );
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onCraft(CraftItemEvent event) {

        TriggerManager.callTrigger(
                new CraftTrigger(plugin.getCharacterManager().getHero((Player) event.getWhoClicked()), event)
        );
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEnchant(EnchantItemEvent event) {

        TriggerManager.callTrigger(
                new EnchantTrigger(plugin.getCharacterManager().getHero(event.getEnchanter()), event)
        );
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBrew(BrewEvent event) {

        try {
            if (brewingPlayers.containsKey(event.getBlock())) {
                TriggerManager.callTrigger(
                        new BrewTrigger(plugin.getCharacterManager().getHero(brewingPlayers.get(event.getBlock())), event)
                );
            }
        } catch (UnknownPlayerException e) {
            // player is offline so remove him
            brewingPlayers.remove(event.getBlock());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInventoryOpen(InventoryOpenEvent event) {

        TriggerManager.callTrigger(
                new InventoryOpenTrigger(plugin.getCharacterManager().getHero((Player) event.getPlayer()), event)
        );
        if (event.getInventory().getType() == InventoryType.BREWING) {
            brewingPlayers.put(((BlockState) event.getInventory().getHolder()).getBlock(), event.getPlayer().getName());
        }
    }
}
