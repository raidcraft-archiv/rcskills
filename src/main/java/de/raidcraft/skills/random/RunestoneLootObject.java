package de.raidcraft.skills.random;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.items.CustomItem;
import de.raidcraft.api.random.RDSObject;
import de.raidcraft.api.random.RDSObjectFactory;
import de.raidcraft.api.random.objects.ItemLootObject;
import de.raidcraft.skills.tables.TRunestone;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author mdoering
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RunestoneLootObject extends ItemLootObject {

    @RDSObjectFactory.Name("runestone")
    public static class RunestoneLootFactory implements RDSObjectFactory {

        @Override
        public RDSObject createInstance(ConfigurationSection config) {

            World world = Bukkit.getWorld(config.getString("world", "world"));
            Location location;
            if (config.isSet("pitch") || config.isSet("yaw")) {
                location = new Location(world,
                        config.getDouble("x"),
                        config.getDouble("y"),
                        config.getDouble("z"),
                        config.getLong("yaw"),
                        config.getLong("pitch"));
            } else {
                location = new Location(world,
                        config.getDouble("x"),
                        config.getDouble("y"),
                        config.getDouble("z"));
            }
            return new RunestoneLootObject(
                    RaidCraft.getCustomItem(config.getString("item")),
                    config.getInt("uses", config.getInt("max-uses", 1)),
                    config.getInt("max-uses", 1),
                    location,
                    config.getString("location-name")
            );
        }
    }

    private CustomItem item;
    private int uses;
    private int maxUses;
    private Location location;
    private String locationName;

    public RunestoneLootObject(CustomItem item, int uses, int maxUses, Location location, String locationName) {

        super(TRunestone.createRunestone(item, uses, maxUses, location, locationName));
        this.item = item;
        this.uses = uses;
        this.maxUses = maxUses;
        this.location = location;
        this.locationName = locationName;
    }

    @Override
    public RDSObject createInstance() {

        return new RunestoneLootObject(item, uses, maxUses, location, locationName);
    }
}
