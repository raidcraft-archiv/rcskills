package de.raidcraft.skills;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.effect.common.QueuedAttack;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * @author Silthus
 */
public final class BukkitEnvironmentManager implements Listener {

    private final SkillsPlugin plugin;

    public BukkitEnvironmentManager(final SkillsPlugin plugin) {

        this.plugin = plugin;
        plugin.registerEvents(this);
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(
                plugin, ConnectionSide.SERVER_SIDE, Packets.Server.ARM_ANIMATION
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {

                // This is the entity whose arm has just moved
                Entity entity = event.getPacket().getEntityModifier(event.getPlayer().getWorld()).read(0);
                if (!(entity instanceof LivingEntity)) {
                    return;
                }
                CharacterTemplate character = BukkitEnvironmentManager.this.plugin.getCharacterManager().getCharacter((LivingEntity) entity);

                int animation = event.getPacket().getIntegers().read(1);
                // See if this is a "move arm" action
                if (animation == 1 && !character.canAttack() && !character.hasEffect(QueuedAttack.class)) {
                    event.setCancelled(true);
                }
            }
        });
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event) {

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.ENCHANTMENT_TABLE) {
            event.getPlayer().sendMessage(ChatColor.RED + "Du kannst keine Zaubertische öffnen.");
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {

        if (plugin.getCommonConfig().health_scale > 0) {
            // lets set the player health scale
            event.getPlayer().setHealthScale(plugin.getCommonConfig().health_scale);
        } else {
            event.getPlayer().setHealthScaled(false);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onFoodGain(FoodLevelChangeEvent event) {

        // make sure we are never above 19 to allow eating
        if (event.getFoodLevel() > 19) {
            event.setFoodLevel(19);
        } else {
            event.setCancelled(true);
        }
    }
}