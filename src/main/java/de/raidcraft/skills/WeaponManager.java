package de.raidcraft.skills;

import de.raidcraft.skills.api.exceptions.CombatException;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

/**
 * @author Silthus
 */
public final class WeaponManager implements Listener {

    private final SkillsPlugin plugin;

    protected WeaponManager(SkillsPlugin plugin) {

        this.plugin = plugin;
        plugin.registerEvents(this);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onInventoryClose(InventoryCloseEvent event) {

        try {
            plugin.getCharacterManager().getHero((Player) event.getPlayer()).checkWeapons(event.getPlayer().getInventory().getHeldItemSlot());
        } catch (CombatException e) {
            ((Player) event.getPlayer()).sendMessage(ChatColor.RED + e.getMessage());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onItemHeldChange(PlayerItemHeldEvent event) {

        try {
            plugin.getCharacterManager().getHero(event.getPlayer()).checkWeapons(event.getNewSlot());
        } catch (CombatException e) {
            event.getPlayer().sendMessage(ChatColor.RED + e.getMessage());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onItemPickup(PlayerPickupItemEvent event) {

        try {
            plugin.getCharacterManager().getHero(event.getPlayer()).checkWeapons(event.getPlayer().getInventory().getHeldItemSlot());
        } catch (CombatException e) {
            event.getPlayer().sendMessage(ChatColor.RED + e.getMessage());
        }
    }
}
