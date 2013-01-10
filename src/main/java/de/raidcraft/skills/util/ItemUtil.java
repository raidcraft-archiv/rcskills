package de.raidcraft.skills.util;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.effects.disabling.Disarm;
import de.raidcraft.skills.items.ArmorType;
import de.raidcraft.skills.items.WeaponType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * @author Silthus
 */
public final class ItemUtil {

    public static boolean isWeapon(Material material) {

        return material != null && WeaponType.fromMaterial(material) != null;
    }

    public static boolean isShield(Material material) {

        return material != null && ArmorType.fromMaterial(material) == ArmorType.SHIELD;
    }

    public static boolean isArmor(Material material) {

        return material != null && ArmorType.fromMaterial(material) != null;
    }

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
            if (contents[i] != null && isWeapon(contents[i].getType())) {
                moveItem(hero, i, contents[i]);
            }
    }
}
