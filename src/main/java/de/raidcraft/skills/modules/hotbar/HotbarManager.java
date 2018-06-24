package de.raidcraft.skills.modules.hotbar;

import de.raidcraft.util.items.EnchantGlow;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class HotbarManager {

    @Getter
    private final HotbarModule module;

    public HotbarManager(HotbarModule module) {
        this.module = module;
    }

    public void addAllHotbars() {
        Bukkit.getOnlinePlayers().forEach(this::addHotbar);
    }

    public void addHotbar(Player player) {
        player.getInventory().setItem(getModule().getConfig().menuItemSlot, createMenuItem());
    }

    public void clearAllHotbars() {
        Bukkit.getOnlinePlayers().forEach(this::clearHotbar);
    }

    public void clearHotbar(Player player) {
        player.getInventory().clear(getModule().getConfig().menuItemSlot);
    }

    private ItemStack createMenuItem() {
        ItemStack itemStack = new ItemStack(Material.matchMaterial(getModule().getConfig().menuItem));

        EnchantGlow.addGlow(itemStack);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GOLD + "-=[ " + ChatColor.DARK_BLUE + "Hotbar Menü" + ChatColor.GOLD + " ]=-");

        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        itemMeta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GREEN + ">> Öffnet das Hotbar Menü mit einem Rechtsklick. <<");

        itemMeta.setLore(lore);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public void openHotbarMenu(Player player) {
        player.sendMessage("Hotbar opened!");
    }
}
