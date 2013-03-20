package de.raidcraft.skills.items;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* @author Silthus
*/
public class ArmorPiece {

    private static final Pattern META_IDENTIFIER = Pattern.compile("^([0-9]+)\\sRüstung");

    private final ItemStack item;
    private int armorValue;

    public ArmorPiece(ItemStack item) {

        this.item = item;
        ItemMeta meta = item.getItemMeta();
        try {
            String armor = ChatColor.stripColor(meta.getLore().get(0)).toLowerCase().trim();
            Matcher matcher = META_IDENTIFIER.matcher(armor);
            if (matcher.matches()) {
                // group one is our armor value
                armorValue = Integer.parseInt(matcher.group(1));
            } else {
                // no meta data is defined so lets grap the default armor value from the config
                armorValue = RaidCraft.getComponent(SkillsPlugin.class).getArmorManager().getDefaultArmorValue(getItemId());
            }
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
    }

    public int getItemId() {

        return item.getTypeId();
    }

    public int getArmorValue() {

        return armorValue;
    }

    public void setArmorValue(int armorValue) {

        this.armorValue = armorValue;
    }
}
