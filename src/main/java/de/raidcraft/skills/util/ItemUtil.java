package de.raidcraft.skills.util;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.items.ArmorType;
import de.raidcraft.api.items.CustomArmor;
import de.raidcraft.api.items.CustomItem;
import de.raidcraft.api.items.CustomItemManager;
import de.raidcraft.api.items.CustomWeapon;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.effects.disabling.Disarm;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * @author Silthus
 */
public final class ItemUtil {

    public static int firstEmpty(ItemStack... inventory) {

        for (int i = 9; i < inventory.length; i++)
            if (inventory[i] == null)
                return i;

        return -1;
    }

    public static boolean moveItem(Hero hero, int slot, ItemStack item) {

        Player player = hero.getPlayer();
        PlayerInventory inv = player.getInventory();
        int empty = firstEmpty(inv.getContents());
        if (empty == -1) {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
            if (slot != -1) {
                inv.clear(slot);
            }
            return false;
        }
        inv.setItem(empty, item);
        if (slot != -1) {
            inv.clear(slot);
        }
        return true;
    }

    public static void disarmCheck(Hero hero) {

        if (!hero.hasEffect(Disarm.class)) {
            return;
        }
        ItemStack contents[] = hero.getPlayer().getInventory().getContents();
        for (int i = 0; i < 9; i++)
            if (contents[i] != null && isWeapon(contents[i])) {
                moveItem(hero, i, contents[i]);
            }
    }

    public static boolean isWeapon(ItemStack itemStack) {

        if (itemStack == null || itemStack.getTypeId() == 0) {
            return false;
        }
        CustomItem item = RaidCraft.getComponent(CustomItemManager.class).getCustomItem(itemStack).getItem();
        return item instanceof CustomWeapon;
    }

    public static CustomWeapon getWeapon(ItemStack itemStack) {

        CustomItem item = RaidCraft.getComponent(CustomItemManager.class).getCustomItem(itemStack).getItem();
        if (item instanceof CustomWeapon) {
            return (CustomWeapon) item;
        }
        return null;
    }

    public static boolean isShield(ItemStack itemStack) {

        if (itemStack == null || itemStack.getTypeId() == 0) {
            return false;
        }
        CustomItem item = RaidCraft.getComponent(CustomItemManager.class).getCustomItem(itemStack).getItem();
        return item instanceof CustomArmor && ((CustomArmor) item).getArmorType() == ArmorType.SHIELD;
    }

    public static boolean isArmor(ItemStack itemStack) {

        if (itemStack == null || itemStack.getTypeId() == 0) {
            return false;
        }
        CustomItem item = RaidCraft.getComponent(CustomItemManager.class).getCustomItem(itemStack).getItem();
        return item instanceof CustomArmor && ((CustomArmor) item).getArmorType() != ArmorType.SHIELD;
    }
}
