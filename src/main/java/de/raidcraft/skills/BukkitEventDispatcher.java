package de.raidcraft.skills;

import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.skills.api.events.RCCombatEvent;
import de.raidcraft.skills.api.trigger.TriggerManager;
import de.raidcraft.skills.trigger.BlockBreakTrigger;
import de.raidcraft.skills.trigger.BlockPlaceTrigger;
import de.raidcraft.skills.trigger.BowFireTrigger;
import de.raidcraft.skills.trigger.BrewTrigger;
import de.raidcraft.skills.trigger.CombatTrigger;
import de.raidcraft.skills.trigger.CraftTrigger;
import de.raidcraft.skills.trigger.EnchantTrigger;
import de.raidcraft.skills.trigger.EntityTargetTrigger;
import de.raidcraft.skills.trigger.FurnaceExtractTrigger;
import de.raidcraft.skills.trigger.InventoryClickTrigger;
import de.raidcraft.skills.trigger.InventoryCloseTrigger;
import de.raidcraft.skills.trigger.InventoryOpenTrigger;
import de.raidcraft.skills.trigger.ItemHeldTrigger;
import de.raidcraft.skills.trigger.ItemPickupTrigger;
import de.raidcraft.skills.trigger.PlayerConsumeTrigger;
import de.raidcraft.skills.trigger.PlayerFishTrigger;
import de.raidcraft.skills.trigger.PlayerInteractTrigger;
import de.raidcraft.skills.trigger.PlayerItemBreakTrigger;
import de.raidcraft.skills.trigger.PlayerLoginTrigger;
import de.raidcraft.skills.trigger.PlayerQuitTrigger;
import de.raidcraft.skills.trigger.PotionSplashTrigger;
import de.raidcraft.skills.trigger.ProjectileHitTrigger;
import de.raidcraft.skills.trigger.ProjectileLaunchTrigger;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public final class BukkitEventDispatcher implements Listener {

    private final SkillsPlugin plugin;
    private final Map<Block, String> brewingPlayers = new HashMap<>();

    public BukkitEventDispatcher(SkillsPlugin plugin) {

        this.plugin = plugin;
        plugin.registerEvents(this);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {

        if (event.getPlayer().hasMetadata("NPC")) {
            return;
        }
        TriggerManager.callSafeTrigger(
                new BlockBreakTrigger(plugin.getCharacterManager().getHero(event.getPlayer()), event)
        );
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {

        if (event.getPlayer().hasMetadata("NPC")) {
            return;
        }
        TriggerManager.callSafeTrigger(
                new BlockPlaceTrigger(plugin.getCharacterManager().getHero(event.getPlayer()), event)
        );
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {

        if (event.getPlayer().hasMetadata("NPC")) {
            return;
        }
        TriggerManager.callSafeTrigger(
                new PlayerInteractTrigger(plugin.getCharacterManager().getHero(event.getPlayer()), event)
        );
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onInventoryClose(InventoryCloseEvent event) {

        TriggerManager.callSafeTrigger(
                new InventoryCloseTrigger(plugin.getCharacterManager().getHero((Player) event.getPlayer()), event)
        );
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onItemHeld(PlayerItemHeldEvent event) {

        TriggerManager.callSafeTrigger(
                new ItemHeldTrigger(plugin.getCharacterManager().getHero(event.getPlayer()), event)
        );
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent event) {

        TriggerManager.callSafeTrigger(
                new InventoryClickTrigger(plugin.getCharacterManager().getHero((Player) event.getWhoClicked()), event)
        );
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onCraft(CraftItemEvent event) {

        TriggerManager.callSafeTrigger(
                new CraftTrigger(plugin.getCharacterManager().getHero((Player) event.getWhoClicked()), event)
        );
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEnchant(EnchantItemEvent event) {

        TriggerManager.callSafeTrigger(
                new EnchantTrigger(plugin.getCharacterManager().getHero(event.getEnchanter()), event)
        );
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFurnaceExtract(FurnaceExtractEvent event) {

        TriggerManager.callSafeTrigger(
                new FurnaceExtractTrigger(plugin.getCharacterManager().getHero(event.getPlayer()), event)
        );
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBrew(BrewEvent event) {

        try {
            if (brewingPlayers.containsKey(event.getBlock())) {
                TriggerManager.callSafeTrigger(
                        new BrewTrigger(plugin.getCharacterManager().getHero(brewingPlayers.get(event.getBlock())), event)
                );
            }
        } catch (UnknownPlayerException e) {
            // player is offline so remove him
            brewingPlayers.remove(event.getBlock());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInventoryOpen(InventoryOpenEvent event) {

        TriggerManager.callSafeTrigger(
                new InventoryOpenTrigger(plugin.getCharacterManager().getHero((Player) event.getPlayer()), event)
        );
        if (event.getInventory().getType() == InventoryType.BREWING) {
            brewingPlayers.put(((BlockState) event.getInventory().getHolder()).getBlock(), event.getPlayer().getName());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityTargetEntity(EntityTargetLivingEntityEvent event) {

        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }
        TriggerManager.callSafeTrigger(
                new EntityTargetTrigger(plugin.getCharacterManager().getCharacter((LivingEntity) event.getEntity()), event)
        );
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onCombat(RCCombatEvent event) {

        TriggerManager.callSafeTrigger(
                new CombatTrigger(event.getHero(), event)
        );
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onProjectileHit(ProjectileHitEvent event) {

        if (event.getEntity().getShooter() == null) {
            return;
        }
        TriggerManager.callSafeTrigger(
                new ProjectileHitTrigger(plugin.getCharacterManager().getCharacter(event.getEntity().getShooter()), event)
        );
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onProjectileFire(ProjectileLaunchEvent event) {

        if (event.getEntity().getShooter() == null) {
            return;
        }
        TriggerManager.callSafeTrigger(
                new ProjectileLaunchTrigger(plugin.getCharacterManager().getCharacter(event.getEntity().getShooter()), event)
        );
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onShootBow(EntityShootBowEvent event) {

        TriggerManager.callSafeTrigger(
                new BowFireTrigger(plugin.getCharacterManager().getCharacter(event.getEntity()), event)
        );
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPotionSplash(PotionSplashEvent event) {

        TriggerManager.callSafeTrigger(
                new PotionSplashTrigger(plugin.getCharacterManager().getCharacter(event.getEntity().getShooter()), event)
        );
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerFishing(PlayerFishEvent event) {

        TriggerManager.callSafeTrigger(
                new PlayerFishTrigger(plugin.getCharacterManager().getCharacter(event.getPlayer()), event)
        );
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onItemBreak(PlayerItemBreakEvent event) {

        TriggerManager.callSafeTrigger(
                new PlayerItemBreakTrigger(plugin.getCharacterManager().getCharacter(event.getPlayer()), event)
        );
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onItemPickup(PlayerPickupItemEvent event) {

        TriggerManager.callSafeTrigger(
                new ItemPickupTrigger(plugin.getCharacterManager().getCharacter(event.getPlayer()), event)
        );
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onConsumeItem(PlayerItemConsumeEvent event) {

        TriggerManager.callSafeTrigger(
                new PlayerConsumeTrigger(plugin.getCharacterManager().getCharacter(event.getPlayer()), event)
        );
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerLogin(PlayerJoinEvent event) {

        TriggerManager.callSafeTrigger(
                new PlayerLoginTrigger(plugin.getCharacterManager().getHero(event.getPlayer()), event)
        );
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {

        TriggerManager.callSafeTrigger(
                new PlayerQuitTrigger(plugin.getCharacterManager().getHero(event.getPlayer()), event)
        );
    }
}
