package de.raidcraft.skills.tables;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.items.CustomItemStack;
import de.raidcraft.skills.SkillsPlugin;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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

    public static TRunestone createRunestone(CustomItemStack customItemStack, int maxUses, int remainingUses, Location location) {

        TRunestone runestone = getRunestone(customItemStack);
        if (runestone != null) return runestone;
        runestone = new TRunestone();
        runestone.setCustomItemId(customItemStack.getItem().getId());
        runestone.setMaxUses(maxUses);
        runestone.setRemainingUses(remainingUses);
        runestone.setWorld(location.getWorld().getName());
        runestone.setX(location.getBlockX());
        runestone.setY(location.getBlockY());
        runestone.setZ(location.getBlockZ());
        runestone.setYaw(location.getYaw());
        runestone.setPitch(location.getPitch());
        RaidCraft.getDatabase(SkillsPlugin.class).save(runestone);
        customItemStack.setMetaDataId(runestone.getId());
        RaidCraft.LOGGER.info("Created runestone with database id " + runestone.getId() + " and meta id " + customItemStack.getMetaDataId());
        return runestone;
    }

    @Id
    private int id;
    private int customItemId;
    private int maxUses;
    private int remainingUses;
    private String world;
    private int x;
    private int y;
    private int z;
    private float yaw;
    private float pitch;
}
