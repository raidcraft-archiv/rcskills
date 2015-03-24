package de.raidcraft.skills.tables;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.items.CustomItem;
import de.raidcraft.api.items.CustomItemStack;
import de.raidcraft.api.items.tooltip.FixedMultilineTooltip;
import de.raidcraft.api.items.tooltip.TooltipSlot;
import de.raidcraft.skills.SkillsPlugin;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import java.sql.Timestamp;

/**
 * @author mdoering
 */
@Getter
@Setter
@Entity
@Table(name = "runestones")
public class TRunestone {

    public static TRunestone getRunestone(CustomItemStack customItemStack) {

        if (customItemStack.getMetaDataId() < 1) {
            return null;
        }
        return RaidCraft.getDatabase(SkillsPlugin.class).find(TRunestone.class, customItemStack.getMetaDataId());
    }

    public static void updateRunestone(TRunestone runestone, int remainingUses) {

        runestone.setRemainingUses(remainingUses);
        RaidCraft.getDatabase(SkillsPlugin.class).save(runestone);
    }

    public static void deleteRunestone(TRunestone runestone) {

        RaidCraft.getDatabase(SkillsPlugin.class).delete(runestone);
    }

    public static CustomItemStack createRunestone(CustomItem item, int uses, int maxUses, Location location, String locationName) {

        CustomItemStack customItemStack = item.createNewItem();
        TRunestone runestone = getRunestone(customItemStack);
        if (runestone != null) return customItemStack;
        runestone = new TRunestone();
        runestone.setCustomItemId(item.getId());
        runestone.setMaxUses(maxUses);
        runestone.setRemainingUses(uses);
        runestone.setWorld(location.getWorld().getName());
        runestone.setX(location.getX());
        runestone.setY(location.getY());
        runestone.setZ(location.getZ());
        runestone.setYaw(location.getYaw());
        runestone.setPitch(location.getPitch());
        RaidCraft.getDatabase(SkillsPlugin.class).save(runestone);
        customItemStack.setMetaDataId(runestone.getId());
        if (locationName == null) {
            locationName = location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ() + " (" + location.getWorld().getName() + ")";
        }
        customItemStack.setTooltip(new FixedMultilineTooltip(TooltipSlot.MISC,
                ChatColor.GREEN + "Aufladungen: " + ChatColor.AQUA + uses + ChatColor.GREEN + "/" + ChatColor.AQUA + maxUses,
                ChatColor.GREEN + "Ort: " + ChatColor.GOLD + locationName
        ));
        customItemStack.rebuild();
        RaidCraft.LOGGER.info("Created runestone with database id " + runestone.getId() + " and meta id " + customItemStack.getMetaDataId());
        return customItemStack;
    }

    @Id
    private int id;
    private int customItemId;
    private int maxUses;
    private int remainingUses;
    private String world;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    @Version
    private Timestamp lastUpdate;
}
