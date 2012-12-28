package de.raidcraft.skills.util;

import de.raidcraft.skills.api.effect.common.Disarm;
import de.raidcraft.skills.api.hero.Hero;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * @author Silthus
 */
public final class ItemUtil {

    public static final Material[] WEAPONS = {
            Material.WOOD_AXE,
            Material.STONE_AXE,
            Material.IRON_AXE,
            Material.DIAMOND_AXE,

            Material.WOOD_SWORD,
            Material.STONE_SWORD,
            Material.IRON_SWORD,
            Material.DIAMOND_SWORD,

            Material.WOOD_SPADE,
            Material.STONE_SPADE,
            Material.IRON_SPADE,
            Material.DIAMOND_SPADE,

            Material.WOOD_HOE,
            Material.STONE_HOE,
            Material.IRON_HOE,
            Material.DIAMOND_HOE
    };

    public static boolean isWeapon(Material material) {

        for (Material weapon : WEAPONS) {
            if (weapon == material) {
                return true;
            }
        }
        return false;
    }

    public static final Material[] ARMOR = {
            Material.LEATHER_HELMET,
            Material.LEATHER_CHESTPLATE,
            Material.LEATHER_LEGGINGS,
            Material.LEATHER_BOOTS,

            Material.IRON_HELMET,
            Material.IRON_CHESTPLATE,
            Material.IRON_LEGGINGS,
            Material.IRON_BOOTS,

            Material.CHAINMAIL_HELMET,
            Material.CHAINMAIL_CHESTPLATE,
            Material.CHAINMAIL_LEGGINGS,
            Material.CHAINMAIL_BOOTS,

            Material.GOLD_HELMET,
            Material.GOLD_CHESTPLATE,
            Material.GOLD_LEGGINGS,
            Material.GOLD_BOOTS,

            Material.DIAMOND_HELMET,
            Material.DIAMOND_CHESTPLATE,
            Material.DIAMOND_LEGGINGS,
            Material.DIAMOND_BOOTS
    };

    public static boolean isArmor(Material material) {

        for (Material armor : ARMOR) {
            if (armor == material) {
                return true;
            }
        }
        return false;
    }

    public static int firstEmpty(ItemStack inventory[]) {

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
            if (slot != -1)
                inv.clear(slot);
            return false;
        }
        inv.setItem(empty, item);
        if (slot != -1)
            inv.clear(slot);
        return true;
    }

    public static void disarmCheck(Hero hero) {

        if (!hero.hasEffect(Disarm.class)) {
            return;
        }
        ItemStack contents[] = hero.getPlayer().getInventory().getContents();
        for (int i = 0; i < 9; i++)
            if (contents[i] != null && isWeapon(contents[i].getType())) {
                moveItem(hero, i, contents[i]);
            }
    }
}
