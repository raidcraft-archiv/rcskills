package de.raidcraft.skills.items;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.WeaponManager;
import de.raidcraft.util.MathUtil;
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
public class Weapon {

    public enum Slot {

        MAIN_HAND,
        OFF_HAND
    }

    public static final Pattern[] LORE_IDENTIFIER = {
            Pattern.compile("^Item\\sLevel\\s([0-9]+)$"),
            Pattern.compile("^Wird beim Benutzen gebunden$"),
            Pattern.compile("^([Einhändig|Beidhändig|Schildhand])\t\t\t([\\w]+)$"),
            Pattern.compile("^([0-9]+)\\-([0-9]+)\\sSchaden\t\t\tTempo\\s(\\d\\.\\d{2})$"),
            Pattern.compile("^([\\d]+)\\s([\\w]+)$")
    };

    private final int taskBarSlot;
    private final ItemStack item;
    private final WeaponType weaponType;
    private final Slot slot;
    private int minDamage;
    private int maxDamage;
    private double swingTime;
    private double modifier = 1.0;

    public Weapon(int taskBarSlot, ItemStack item, Slot slot) {

        this.taskBarSlot = taskBarSlot;
        this.item = item;
        this.slot = slot;
        this.weaponType = WeaponType.fromMaterial(item.getType());
        ItemMeta meta = item.getItemMeta();
        try {
            if (meta.hasLore()) {
                String weapon = ChatColor.stripColor(meta.getLore().get(0)).toLowerCase().trim();
                Matcher matcher = LORE_IDENTIFIER[3].matcher(weapon);
                if (matcher.matches()) {
                    // group one is our weapon value
                    minDamage = Integer.parseInt(matcher.group(1));
                    maxDamage = Integer.parseInt(matcher.group(2));
                    swingTime = Double.parseDouble(matcher.group(3));
                    return;
                }
            }
            // no meta data is defined so lets grap the default armor value from the config
            WeaponManager weaponManager = RaidCraft.getComponent(SkillsPlugin.class).getWeaponManager();
            WeaponManager.DefaultWeaponConfig damage = weaponManager.getDefaultMinMaxDamage(item.getTypeId());
            if (damage != null) {
                minDamage = damage.getMinDamage();
                maxDamage = damage.getMaxDamage();
                swingTime = damage.getSwingTime();
            }
            save();
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
    }

    public int getTaskBarSlot() {

        return taskBarSlot;
    }

    public int getItemId() {

        return item.getTypeId();
    }

    public ItemStack getItem() {

        return item;
    }

    public WeaponType getWeaponType() {

        return weaponType;
    }

    public Slot getSlot() {

        return slot;
    }

    public int getMinDamage() {

        return minDamage;
    }

    public void setMinDamage(int minDamage) {

        this.minDamage = minDamage;
    }

    public int getMaxDamage() {

        return maxDamage;
    }

    public void setMaxDamage(int maxDamage) {

        this.maxDamage = maxDamage;
    }

    public double getModifier() {

        return modifier;
    }

    public void setModifier(double modifier) {

        this.modifier = modifier;
    }

    public double getSwingTime() {

        return swingTime;
    }

    public void setSwingTime(double swingTime) {

        this.swingTime = swingTime;
    }

    public int getDamage() {

        return (int) ((MathUtil.RANDOM.nextInt(maxDamage - minDamage + 1) + minDamage) * modifier);
    }

    public void save() {

        ItemMeta itemMeta = item.getItemMeta();
        List<String> lore;
        if (itemMeta.hasLore()) {
            lore = itemMeta.getLore();
        } else {
            lore = new ArrayList<>(1);
        }
        String swingTime = Double.toString(getSwingTime());
        if (swingTime.length() < 3) {
            swingTime = swingTime + "0";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(getMinDamage()).append("-").append(getMaxDamage()).append(" Schaden - Tempo ").append(swingTime);
        if (lore.size() > 0) {
            lore.set(0, sb.toString());
        } else {
            lore.add(sb.toString());
        }
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
    }
}
