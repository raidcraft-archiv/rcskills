package de.raidcraft.skills;

import de.raidcraft.util.CustomItemUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerChangedMainHandEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

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

        // lets ceck the first slot and checkweapons will always check the second slot
        plugin.getCharacterManager().getHero((Player) event.getPlayer()).checkWeapons();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onItemPickup(PlayerPickupItemEvent event) {

        int pickupSlot = CustomItemUtil.getPickupSlot(event);
        if (pickupSlot == CustomItemUtil.MAIN_WEAPON_SLOT|| pickupSlot == CustomItemUtil.OFFHAND_WEAPON_SLOT) {
            plugin.getCharacterManager().getHero(event.getPlayer()).checkWeapons();
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onItemChange(PlayerItemHeldEvent event) {
        if (event.getNewSlot() == event.getPreviousSlot()) return;
        if (event.getNewSlot() == CustomItemUtil.MAIN_WEAPON_SLOT || event.getNewSlot() == CustomItemUtil.OFFHAND_WEAPON_SLOT) {
            plugin.getCharacterManager().getHero(event.getPlayer()).checkWeapons();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        plugin.getCharacterManager().getHero(event.getPlayer()).checkWeapons();
    }
}
