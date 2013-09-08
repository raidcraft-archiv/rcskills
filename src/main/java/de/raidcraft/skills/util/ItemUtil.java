package de.raidcraft.skills.util;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.effects.disabling.Disarm;
import de.raidcraft.util.CustomItemUtil;
import org.bukkit.inventory.ItemStack;

/**
 * @author Silthus
 */
public final class ItemUtil {

    public static void disarmCheck(Hero hero) {

        if (!hero.hasEffect(Disarm.class)) {
            return;
        }
        ItemStack contents[] = hero.getPlayer().getInventory().getContents();
        for (int i = 0; i < 9; i++)
            if (contents[i] != null && CustomItemUtil.isWeapon(contents[i])) {
                CustomItemUtil.moveItem(hero.getPlayer(), i, contents[i]);
            }
    }
}
