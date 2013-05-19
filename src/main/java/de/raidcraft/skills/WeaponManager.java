package de.raidcraft.skills;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.items.CustomArmor;
import de.raidcraft.api.items.CustomItemStack;
import de.raidcraft.api.items.CustomWeapon;
import de.raidcraft.api.items.EquipmentSlot;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.config.CustomConfig;
import de.raidcraft.skills.util.ItemUtil;
import de.raidcraft.util.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Silthus
 */
public final class WeaponManager implements Listener {

    private static final String CONFIG_NAME = "weapons";
    private final SkillsPlugin plugin;
    // maps the weapon (itemId) to the min/max damage (key/value)
    private final Map<Integer, DefaultWeaponConfig> defaultWeapons = new HashMap<>();

    protected WeaponManager(SkillsPlugin plugin) {

        this.plugin = plugin;
        plugin.registerEvents(this);
        load();
    }

    private void load() {

        defaultWeapons.clear();
        ConfigurationSection config = CustomConfig.getConfig(CONFIG_NAME).getSafeConfigSection("weapons");
        Set<String> keys = config.getKeys(false);
        if (keys == null || keys.size() < 1) {
            plugin.getLogger().warning("No weapons configured in custom weapons.yml config.");
            return;
        }
        for (String key : keys) {
            Material item = ItemUtils.getItem(key);
            if (item != null) {
                int minDamage = config.getInt(key + ".min", 0);
                int maxDamage = config.getInt(key + ".max", 0);
                double swingTime = config.getDouble(key + ".swing-time", 1.5);
                defaultWeapons.put(item.getId(), new DefaultWeaponConfig(minDamage, maxDamage, swingTime));
            } else {
                plugin.getLogger().warning("Wrong weapon item configured in custom config: " + config.getName() + " - " + key);
            }
        }
    }

    public void reload() {

        CustomConfig.getConfig(CONFIG_NAME).reload();
        load();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onInventoryClose(InventoryCloseEvent event) {

        PlayerInventory inventory = event.getPlayer().getInventory();
        checkForWeapons((Player) event.getPlayer(), inventory, inventory.getHeldItemSlot());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onItemHeldChange(PlayerItemHeldEvent event) {

        checkForWeapons(event.getPlayer(), event.getPlayer().getInventory(), event.getNewSlot());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onItemBreak(PlayerItemBreakEvent event) {

        PlayerInventory inventory = event.getPlayer().getInventory();
        checkForWeapons(event.getPlayer(), inventory, inventory.getHeldItemSlot());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onItemPickup(PlayerPickupItemEvent event) {

        PlayerInventory inventory = event.getPlayer().getInventory();
        checkForWeapons(event.getPlayer(), inventory, inventory.getHeldItemSlot());
    }

    private boolean checkForWeapons(Player player, PlayerInventory inventory, int heldItemSlot) {

        // lets check all equiped weapons and adjust the player accordingly
        Hero hero = plugin.getCharacterManager().getHero(player);
        ItemStack itemInHand = inventory.getItemInHand();
        hero.clearWeapons();
        if (!ItemUtil.isWeapon(itemInHand)) {
            if (itemInHand != null && itemInHand.getTypeId() != 0 && defaultWeapons.containsKey(itemInHand.getTypeId())) {

            } else {
                return false;
            }
        }
        CustomItemStack customItem = RaidCraft.getCustomItem(itemInHand);
        if (customItem == null) {
            return false;
        }
        CustomWeapon weapon = (CustomWeapon) customItem.getItem();
        if (weapon.getEquipmentSlot() == EquipmentSlot.SHIELD_HAND) {
            hero.sendMessage(ChatColor.RED + "Du kannst diese Waffe nur in deiner Schildhand tragen.");
            ItemUtil.moveItem(hero, heldItemSlot, itemInHand);
            return false;
        }
        if (hasOffHandWeapon(inventory, heldItemSlot)) {
            if (weapon.getEquipmentSlot() == EquipmentSlot.TWO_HANDED) {
                hero.sendMessage(ChatColor.RED + "Du benötigst beide Hände um diese Waffe zu tragen.");
                ItemUtil.moveItem(hero, heldItemSlot + 1, inventory.getItem(heldItemSlot + 1));
            } else {
                // lets get the second weapon and check everything
                CustomItemStack offHandWeapon = RaidCraft.getCustomItem(inventory.getItem(heldItemSlot + 1));
                try {
                    if (checkWeapon(hero, (CustomWeapon) offHandWeapon.getItem())) {
                        hero.setWeapon((CustomWeapon) offHandWeapon.getItem());
                    }
                } catch (CombatException e) {
                    hero.sendMessage(ChatColor.RED + e.getMessage());
                    ItemUtil.moveItem(hero, heldItemSlot + 1, offHandWeapon.getHandle());
                }
            }
        } else if (heldItemSlot + 1 < 9) {
            CustomItemStack itemStack = RaidCraft.getCustomItem(inventory.getItem(heldItemSlot + 1));
            if (itemStack != null && ItemUtil.isArmor(inventory.getItem(heldItemSlot + 1))) {
                // check for a equiped shield
                hero.setArmor((CustomArmor) itemStack.getItem());
            }
        } else {
            hero.removeArmor(EquipmentSlot.SHIELD_HAND);
            hero.removeWeapon(EquipmentSlot.SHIELD_HAND);
        }
        hero.setWeapon(weapon);
        return true;
    }

    private boolean checkWeapon(Hero hero, CustomWeapon weapon) throws CombatException {

        if (weapon == null) {
            return false;
        }
        if (!weapon.isMeetingAllRequirements(hero.getPlayer())) {
            throw new CombatException(weapon.getResolveReason(hero.getPlayer()));
        }
        return true;
    }

    private boolean hasOffHandWeapon(PlayerInventory inventory, int heldItemSlot) {

        return heldItemSlot + 1 < 9
                && inventory.getItem(heldItemSlot + 1) != null
                && inventory.getItem(heldItemSlot + 1).getTypeId() != 0
                && ItemUtil.isWeapon(inventory.getItem(heldItemSlot + 1));
    }

    public DefaultWeaponConfig getDefaultMinMaxDamage(int itemid) {

        return defaultWeapons.get(itemid);
    }

    public static class DefaultWeaponConfig {

        private final int minDamage;
        private final int maxDamage;
        private final double swingTime;

        public DefaultWeaponConfig(int minDamage, int maxDamage, double swingTime) {

            this.minDamage = minDamage;
            this.maxDamage = maxDamage;
            this.swingTime = swingTime;
        }

        public int getMinDamage() {

            return minDamage;
        }

        public int getMaxDamage() {

            return maxDamage;
        }

        public double getSwingTime() {

            return swingTime;
        }
    }
}
