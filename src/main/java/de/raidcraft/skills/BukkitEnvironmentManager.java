package de.raidcraft.skills;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.effect.common.QueuedAttack;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.resource.Resource;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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

                int animation = event.getPacket().getIntegers().read(0);
                // See if this is a "move arm" action
                if (animation == 0 && !character.canAttack() && !character.hasEffect(QueuedAttack.class)) {
                    event.setCancelled(true);
                }
            }
        });
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event) {

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.ENCHANTING_TABLE) {
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

        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (plugin.getCommonConfig().disable_vanilla_hunger) {
            // make sure we are never above 19 to allow eating
            // and prevent any food changes
            event.setFoodLevel(19);
        } else {
            // make sure we are never above 19 to allow eating
            if (event.getFoodLevel() > 19) {
                event.setFoodLevel(19);
            } else if (event.getFoodLevel() > 16) {
                Hero hero = plugin.getCharacterManager().getHero((Player) event.getEntity());
                Resource health = hero.getResource("health");
                if (health != null) health.setRegenEnabled(true);
            } else {
                Hero hero = plugin.getCharacterManager().getHero((Player) event.getEntity());
                Resource health = hero.getResource("health");
                if (health != null) health.setRegenEnabled(false);
            }
        }
    }
}