package de.raidcraft.skills;

import de.raidcraft.skills.api.events.RCCombatEvent;
import de.raidcraft.skills.api.events.RCEntityDeathEvent;
import de.raidcraft.skills.api.events.RCMaxHealthChangeEvent;
import de.raidcraft.skills.api.trigger.TriggerManager;
import de.raidcraft.skills.trigger.*;
import net.citizensnpcs.api.event.NPCRightClickEvent;
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
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.projectiles.ProjectileSource;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public final class BukkitEventDispatcher implements Listener {

    private final SkillsPlugin plugin;
    private final Map<Block, Player> brewingPlayers = new HashMap<>();

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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerEggThrow(PlayerEggThrowEvent event) {

        TriggerManager.callSafeTrigger(
                new PlayerEggThrowTrigger(plugin.getCharacterManager().getHero(event.getPlayer()), event)
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

        if (brewingPlayers.containsKey(event.getBlock())) {
            TriggerManager.callSafeTrigger(
                    new BrewTrigger(plugin.getCharacterManager().getHero(brewingPlayers.get(event.getBlock())), event)
            );
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInventoryOpen(InventoryOpenEvent event) {

        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        TriggerManager.callSafeTrigger(
                new InventoryOpenTrigger(plugin.getCharacterManager().getHero((Player) event.getPlayer()), event)
        );
        if (event.getInventory().getType() == InventoryType.BREWING) {
            brewingPlayers.put(((BlockState) event.getInventory().getHolder()).getBlock(),
                    (Player) event.getPlayer());
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

        ProjectileSource shooter = event.getEntity().getShooter();
        if (shooter == null || !(shooter instanceof LivingEntity)) {
            return;
        }
        TriggerManager.callSafeTrigger(
                new ProjectileHitTrigger(plugin.getCharacterManager().getCharacter((LivingEntity) shooter), event)
        );
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onProjectileFire(ProjectileLaunchEvent event) {

        ProjectileSource shooter = event.getEntity().getShooter();
        if (shooter == null || !(shooter instanceof LivingEntity)) {
            return;
        }
        TriggerManager.callSafeTrigger(
                new ProjectileLaunchTrigger(plugin.getCharacterManager().getCharacter((LivingEntity) shooter), event)
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

        ProjectileSource shooter = event.getEntity().getShooter();
        if (shooter == null || !(shooter instanceof LivingEntity)) {
            return;
        }
        TriggerManager.callSafeTrigger(
                new PotionSplashTrigger(plugin.getCharacterManager().getCharacter((LivingEntity) shooter), event)
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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onItemDrop(PlayerDropItemEvent event) {

        TriggerManager.callSafeTrigger(
                new ItemDropTrigger(plugin.getCharacterManager().getHero(event.getPlayer()), event)
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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerShearSheep(PlayerShearEntityEvent event) {

        TriggerManager.callSafeTrigger(
                new PlayerShearTrigger(plugin.getCharacterManager().getHero(event.getPlayer()), event)
        );
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onRCEntityDeath(RCEntityDeathEvent event) {

        TriggerManager.callSafeTrigger(
                new EntityDeathTrigger(event.getCharacter(), event)
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onVehicleExit(VehicleExitEvent event) {

        TriggerManager.callSafeTrigger(
                new PlayerVehicleExitTrigger(plugin.getCharacterManager().getCharacter(event.getExited()), event)
        );
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onMaxHealthChange(RCMaxHealthChangeEvent event) {

        TriggerManager.callSafeTrigger(
                new MaxHealthChangeTrigger(event.getCharacter(), event)
        );
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onNPCClick(NPCRightClickEvent event) {

        TriggerManager.callSafeTrigger(
                new NPCRightClickTrigger(plugin.getCharacterManager().getHero(event.getClicker()), event)
        );
    }
}
