package de.raidcraft.skills;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

/**
 * @author Silthus
 */
public final class BukkitEnvironmentManager implements Listener {

    private final SkillsPlugin plugin;

    public BukkitEnvironmentManager(SkillsPlugin plugin) {

        this.plugin = plugin;
        plugin.registerEvents(this);
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(
                plugin,
                ConnectionSide.SERVER_SIDE,
                Packets.Server.UPDATE_HEALTH
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {

                PacketContainer packet = event.getPacket().deepClone();
                Player player = event.getPlayer();
                int health = (player.getHealth() / player.getMaxHealth()) * 20;
                packet.getIntegers().write(0, health);
                event.setPacket(packet);
            }
        });
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onFoodGain(FoodLevelChangeEvent event) {

        // make sure we are never above 19 to allow eating
        if (event.getFoodLevel() > 19) {
            event.setFoodLevel(19);
        }
    }
}
