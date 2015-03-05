package de.raidcraft.skills.tables;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.items.CustomItemStack;
import de.raidcraft.skills.SkillsPlugin;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Optional;

/**
 * @author mdoering
 */
@Getter
@Setter
@Entity
@Table(name = "runestones")
public class TRunestone {

    public static Optional<TRunestone> getRunestone(CustomItemStack customItemStack) {

        if (customItemStack.getMetaDataId() < 1) {
            return Optional.empty();
        }
        return Optional.ofNullable(RaidCraft.getDatabase(SkillsPlugin.class).find(TRunestone.class, customItemStack.getMetaDataId()));
    }

    public static void updateRunestone(TRunestone runestone, int remainingUses) {

        EbeanServer database = RaidCraft.getDatabase(SkillsPlugin.class);
        runestone.setRemainingUses(remainingUses);
        database.update(runestone);
    }

    public static void deleteRunestone(TRunestone runestone) {

        RaidCraft.getDatabase(SkillsPlugin.class).delete(runestone);
    }

    public static TRunestone createRunestone(CustomItemStack customItemStack, int maxUses, int remainingUses, Location location) {

        Optional<TRunestone> entry = getRunestone(customItemStack);
        if (entry.isPresent()) return entry.get();
        TRunestone runestone = new TRunestone();
        runestone.setCustomItemId(customItemStack.getItem().getId());
        runestone.setMaxUses(maxUses);
        runestone.setRemainingUses(remainingUses);
        runestone.setWorld(location.getWorld().getName());
        runestone.setX(location.getX());
        runestone.setY(location.getY());
        runestone.setZ(location.getZ());
        runestone.setYaw(location.getYaw());
        runestone.setPitch(location.getPitch());
        RaidCraft.getDatabase(SkillsPlugin.class).save(runestone);
        customItemStack.setMetaDataId(runestone.getId());
        return runestone;
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
}
