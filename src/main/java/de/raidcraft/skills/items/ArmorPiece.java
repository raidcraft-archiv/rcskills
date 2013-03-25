package de.raidcraft.skills.items;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* @author Silthus
*/
public class ArmorPiece {

    private static final Pattern META_IDENTIFIER = Pattern.compile("^([0-9]+)\\sRüstung$");

    private final ItemStack item;
    private final ArmorType type;
    private int armorValue;

    public ArmorPiece(ItemStack item) {

        this.item = item;
        type = ArmorType.fromItemId(item.getTypeId());
        ItemMeta meta = item.getItemMeta();
        try {
            if (meta.hasLore()) {
                String armor = ChatColor.stripColor(meta.getLore().get(0)).toLowerCase().trim();
                Matcher matcher = META_IDENTIFIER.matcher(armor);
                if (matcher.matches()) {
                    // group one is our armor value
                    armorValue = Integer.parseInt(matcher.group(1));
                    return;
                }
            }
            // no meta data is defined so lets grap the default armor value from the config
            armorValue = RaidCraft.getComponent(SkillsPlugin.class).getArmorManager().getDefaultArmorValue(getItemId());
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

    public ArmorType getType() {

        return type;
    }

    public void save() {

        ItemMeta itemMeta = item.getItemMeta();
        List<String> lore;
        if (itemMeta.hasLore()) {
            lore = itemMeta.getLore();
        } else {
            lore = new ArrayList<>();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(getArmorValue()).append(" Rüstung");
        lore.set(0, sb.toString());
    }
}
