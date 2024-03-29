package de.raidcraft.skills;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.items.*;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.combat.ProjectileType;
import de.raidcraft.skills.api.combat.ThreatTable;
import de.raidcraft.skills.api.combat.action.Attack;
import de.raidcraft.skills.api.combat.action.PhysicalAttack;
import de.raidcraft.skills.api.combat.action.RangedAttack;
import de.raidcraft.skills.api.combat.callback.LocationCallback;
import de.raidcraft.skills.api.combat.callback.ProjectileCallback;
import de.raidcraft.skills.api.combat.callback.RangedCallback;
import de.raidcraft.skills.api.combat.callback.SourcedRangeCallback;
import de.raidcraft.skills.api.effect.common.QueuedAttack;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.trigger.TriggerHandler;
import de.raidcraft.skills.api.trigger.TriggerManager;
import de.raidcraft.skills.api.trigger.TriggerPriority;
import de.raidcraft.skills.api.trigger.Triggered;
import de.raidcraft.skills.trigger.AttackTrigger;
import de.raidcraft.util.CustomItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.projectiles.ProjectileSource;

import java.util.*;

/**
 * @author Silthus
 */
public final class CombatManager implements Listener, Triggered {

    public static final Set<EntityDamageByEntityEvent> FAKED_EVENTS = new HashSet<>();
    private final SkillsPlugin plugin;
    private final Map<Integer, SourcedRangeCallback<RangedCallback>> entityHitCallbacks = new HashMap<>();
    private final Map<Integer, SourcedRangeCallback<LocationCallback>> locationCallbacks = new HashMap<>();
    private final Map<Integer, SourcedRangeCallback> rangedAttacks = new HashMap<>();

    protected CombatManager(SkillsPlugin plugin) {

        this.plugin = plugin;
        plugin.registerEvents(this);
        TriggerManager.registerListeners(this);
    }

    public static EntityDamageByEntityEvent fakeDamageEvent(Attack<CharacterTemplate, CharacterTemplate> action) {

        return fakeDamageEvent(action.getSource(), action);
    }

    public static EntityDamageByEntityEvent fakeDamageEvent(CharacterTemplate attacker, Attack<?, CharacterTemplate> action) {

        if (action.isOfAttackType(EffectType.MAGICAL)) {
            return fakeDamageEvent(attacker, action, EntityDamageEvent.DamageCause.MAGIC);
        } else {
            return fakeDamageEvent(attacker, action, EntityDamageEvent.DamageCause.ENTITY_ATTACK);
        }
    }

    public static EntityDamageByEntityEvent fakeDamageEvent(CharacterTemplate attacker, Attack<?, CharacterTemplate> action, EntityDamageByEntityEvent.DamageCause cause) {

        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(
                attacker.getEntity(),
                action.getTarget().getEntity(),
                cause,
                action.getDamage());
        // we need to check for our own faked events to avoid loops
        FAKED_EVENTS.add(event);
        RaidCraft.callEvent(event);
        FAKED_EVENTS.remove(event);
        return event;
    }

    public void queueRangedAttack(final SourcedRangeCallback rangedAttack) {

        // remove the callback from the queue after the configured time
        rangedAttack.setTaskId(Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {

                rangedAttacks.remove(rangedAttack.getTaskId());
            }
        }, plugin.getCommonConfig().callback_purge_time));

        rangedAttacks.put(rangedAttack.getTaskId(), rangedAttack);

        if (rangedAttack.getSource() instanceof Hero) {
            ((Hero) rangedAttack.getSource()).debug("Queued Range Entity Callback - " + rangedAttack.getTaskId());
        }
    }

    public void queueEntityCallback(final SourcedRangeCallback<RangedCallback> sourcedCallback) {

        // remove the callback from the queue after the configured time
        sourcedCallback.setTaskId(Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {

                entityHitCallbacks.remove(sourcedCallback.getTaskId());
            }
        }, plugin.getCommonConfig().callback_purge_time));
        entityHitCallbacks.put(sourcedCallback.getTaskId(), sourcedCallback);
        if (sourcedCallback.getSource() instanceof Hero) {
            ((Hero) sourcedCallback.getSource()).debug("Queued Range Entity Callback - " + sourcedCallback.getTaskId());
        }
    }

    public void queueLocationCallback(final SourcedRangeCallback<LocationCallback> sourcedCallback) {

        if (sourcedCallback.getCallback() == null) {
            return;
        }
        // remove the callback from the queue after the configured time
        sourcedCallback.setTaskId(Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {

                locationCallbacks.remove(sourcedCallback.getTaskId());
            }
        }, plugin.getCommonConfig().callback_purge_time));
        locationCallbacks.put(sourcedCallback.getTaskId(), sourcedCallback);
        if (sourcedCallback.getSource() instanceof Hero) {
            ((Hero) sourcedCallback.getSource()).debug("Queued Range Location Callback - " + sourcedCallback.getTaskId());
        }
    }

    public boolean isQueuedRangeAttack(Projectile projectile) {

        boolean hitCallback = entityHitCallbacks.values().stream().anyMatch(c -> c.getProjectile().equals(projectile));
        boolean locationCallback = locationCallbacks.values().stream().anyMatch(c -> c.getProjectile().equals(projectile));
        boolean rangedCallback = rangedAttacks.values().stream().anyMatch(c -> c.getProjectile().equals(projectile));
        return hitCallback || locationCallback || rangedCallback;
    }

    @TriggerHandler(ignoreCancelled = true, filterTargets = false, priority = TriggerPriority.LOWEST)
    public void onAttack(AttackTrigger trigger) throws CombatException {

        checkPvPAttack(trigger.getSource(), trigger.getAttack().getTarget());
    }

    public void checkPvPAttack(CharacterTemplate attackerChar, CharacterTemplate victimChar) throws CombatException {

        if (!(attackerChar instanceof Hero) || !(victimChar instanceof Hero)) {
            return;
        }
        Hero attacker = (Hero) attackerChar;
        Hero victim = (Hero) victimChar;

        if (victim.isPvPEnabled() && attacker.isPvPEnabled()) {
            return;
        }
        // lets check some advanced stuff first, like if the attacking player has pvp disabled and the victim has pvp enabled
        if (victim.isPvPEnabled()) {
            attacker.setPvPEnabled(true);
            attacker.sendMessage(ChatColor.RED + "Dein PvP Status wurde auf aktiv gesetzt!");
        } else {
            throw new CombatException("Dein Ziel hat PvP nicht aktiviert und kann nicht angegriffen werden!");
        }
    }

    /*//////////////////////////////////////////////////////////////////////////////
    //      Hooked Bukkit events for handling all the combat stuff
    //////////////////////////////////////////////////////////////////////////////*/

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onTarget(EntityTargetLivingEntityEvent event) {

        if (!(event.getEntity() instanceof Creature)) {
            return;
        }

        CharacterTemplate creature = plugin.getCharacterManager().getCharacter((LivingEntity) event.getEntity());
        CharacterTemplate target = plugin.getCharacterManager().getCharacter(event.getTarget());

	    if (creature == null || target == null) {
		    return;
	    }

        if (target.isFriendly(creature)) {
            event.setCancelled(true);
        }

        // lets target the target with the highest threat
        ThreatTable.ThreatLevel threat = creature.getThreatTable().getHighestThreat();
        if (threat == null) {
            return;
        }
        LivingEntity entity = threat.getTarget().getEntity();
        event.setTarget(entity);
        ((Creature) creature.getEntity()).setTarget(entity);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onFriendlyAttack(EntityDamageByEntityEvent event) {

        if (FAKED_EVENTS.contains(event)) {
            return;
        }
        if (!(event.getEntity() instanceof LivingEntity) || !(event.getDamager() instanceof LivingEntity)) {
            return;
        }

        CharacterTemplate victim = plugin.getCharacterManager().getCharacter((LivingEntity) event.getEntity());
        CharacterTemplate attacker = plugin.getCharacterManager().getCharacter((LivingEntity) event.getDamager());

	    if (victim == null || attacker == null) {
		    return;
	    }

        if (attacker.isFriendly(victim)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onAttack(EntityDamageByEntityEvent event) {

        if (FAKED_EVENTS.contains(event)) {
            return;
        }
        if (event.getCause() == EntityDamageEvent.DamageCause.CUSTOM) {
            return;
        }
        if (event.getDamage() == 0) {
            return;
        }
        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }
        if (event.getDamager() instanceof Projectile || event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            // the projectile callbacks are handled in the CombatManager
            return;
        }

        CharacterTemplate attacker = null;
        if (event.getDamager() instanceof LivingEntity) {
            attacker = plugin.getCharacterManager().getCharacter((LivingEntity) event.getDamager());
        }

        if (attacker == null) {
            return;
        }

        try {
            if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {

                // lets check if the player is attacking with the wrong item slot
                if (attacker instanceof Hero) {
                    if (((Hero) attacker).getPlayer().getInventory().getHeldItemSlot() != 0) {
                        if (CustomItemUtil.isWeapon(((Hero) attacker).getPlayer().getItemInHand())) {
                            ((Hero) attacker).sendMessage(ChatColor.RED
                                    + "Du musst deine Waffe in deinen ersten Hotbarslot legen um damit Schaden zu machen!");
                        }
                    }
                    for (CustomItemStack item : attacker.getWeapons()) {
                        if (item.getItem() instanceof CustomWeapon) {
                            CustomWeapon weapon = (CustomWeapon) item.getItem();
                            if (weapon.getWeaponType() == WeaponType.BOW || weapon.getWeaponType() == WeaponType.MAGIC_WAND) {
                                event.setCancelled(true);
                                ((Hero) attacker).sendMessage(ChatColor.RED + "Du kannst nicht mit einer Fernkampf Waffe im Nahkampf angreifen.");
                                return;
                            }
                        }
                    }
                }
                PhysicalAttack physicalAttack;
                // lets check for skills that are queued and allow the attack without setting the weapons swing cooldown
                if (attacker.hasEffect(QueuedAttack.class)) {
                    physicalAttack = new PhysicalAttack(event, attacker.getDamage());
                } else {
                    // if not swing the weapons normally
                    if (!attacker.canAttack()) {
                        event.setCancelled(true);
                        return;
                    }
                    physicalAttack = new PhysicalAttack(event, attacker.getDamage() + attacker.swingWeapons());
                    physicalAttack.addWeapons(attacker.getWeapons());
                    physicalAttack.addAttackTypes(EffectType.DEFAULT_ATTACK);
                    physicalAttack.setKnockback(true);
                }

                physicalAttack.run();
                event.setCancelled(true);
            }
        } catch (CombatException e) {
            if (attacker instanceof Hero) {
                ((Hero) attacker).sendMessage(ChatColor.RED + e.getMessage());
            }
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void shootBowEvent(EntityShootBowEvent event) {

        if (!(event.getProjectile() instanceof Projectile)) {
            return;
        }
        if (isQueuedRangeAttack((Projectile) event.getProjectile())) {
            return;
        }
        try {
            CharacterTemplate source = plugin.getCharacterManager().getCharacter(event.getEntity());
            if (source instanceof Hero) {
                int heldItemSlot = ((Hero) source).getPlayer().getInventory().getHeldItemSlot();
                if (heldItemSlot == InventorySlot.OFFHAND_WEAPON_SLOT.getSlot()) {
                    ((Hero) source).sendMessage(ChatColor.RED
                            + "Du musst Zweihand Waffen in deinen ersten Hotbarslot legen um sie zu benutzen.");
                    event.setCancelled(true);
                    return;
                } else if (heldItemSlot != InventorySlot.MAIN_WEAPON_SLOT.getSlot()) {
                    ((Hero) source).sendMessage(ChatColor.RED
                            + "Du musst den Bogen in deinen ersten Hotbarslot legen um ihn zu benutzen.");
                    event.setCancelled(true);
                    return;
                }
            }
            source.triggerCombat(source);
            // queue all ranged attacks to enable tracking of default attacks with projectiles
            RangedAttack<ProjectileCallback> rangedAttack = new RangedAttack<>(source, event);
            rangedAttack.addAttackTypes(EffectType.DEFAULT_ATTACK);
            rangedAttack.addWeapons(source.getWeapons());
            new SourcedRangeCallback<>(rangedAttack).queueCallback();
        } catch (CombatException ignored) {
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void fireProjectileEvent(ProjectileLaunchEvent event) {

        EntityType type = event.getEntityType();
        if (type != EntityType.ARROW || type != EntityType.FIREBALL || type != EntityType.SPLASH_POTION) {
            return;
        }
        if (isQueuedRangeAttack(event.getEntity())) {
            return;
        }
        try {
            ProjectileSource shooter = event.getEntity().getShooter();
            if (shooter == null || !(shooter instanceof LivingEntity)) {
                return;
            }
            CharacterTemplate source = plugin.getCharacterManager().getCharacter((LivingEntity) shooter);
            if (source instanceof Hero) {
                int heldItemSlot = ((Hero) source).getPlayer().getInventory().getHeldItemSlot();
                CustomItemStack weapon = source.getWeapon(EquipmentSlot.SHIELD_HAND);
                if (CustomItemUtil.OFFHAND_WEAPON_SLOT == heldItemSlot && weapon != null) {
                    CustomWeapon customWeapon = CustomItemUtil.getWeapon(weapon);
                    if (customWeapon.getWeaponType().getEquipmentSlot() == EquipmentSlot.TWO_HANDED) {
                        ((Hero) source).sendMessage(ChatColor.RED
                                + "Du musst Zweihand Waffen in deinen ersten Hotbarslot legen um sie benutzen zu können.");
                        event.setCancelled(true);
                    }
                }
            }
            source.triggerCombat(source);
            // queue all ranged attacks to enable tracking of default attacks with projectiles
            RangedAttack<ProjectileCallback> rangedAttack = new RangedAttack<>(source, ProjectileType.valueOf(event.getEntity()));
            rangedAttack.addAttackTypes(EffectType.DEFAULT_ATTACK);
            rangedAttack.setProjectile(event.getEntity());
            new SourcedRangeCallback<>(rangedAttack).queueCallback();
        } catch (CombatException ignored) {
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void projectileHitEvent(ProjectileHitEvent event) {

        ProjectileSource shooter = event.getEntity().getShooter();
        if (shooter == null || !(shooter instanceof LivingEntity)) {
            return;
        }
        CharacterTemplate source = plugin.getCharacterManager().getCharacter((LivingEntity) shooter);
        // iterate over our queued callbacks
        new ArrayList<>(locationCallbacks.values()).stream()
                .filter(sourcedCallback -> sourcedCallback.getProjectile().equals(event.getEntity())
                        && sourcedCallback.getSource().equals(source)).forEach(sourcedCallback -> {
            try {
                locationCallbacks.remove(sourcedCallback.getTaskId());
                sourcedCallback.getCallback().run(event.getEntity().getLocation());
            } catch (CombatException e) {
                if (source instanceof Hero) {
                    ((Hero) source).sendMessage(ChatColor.RED + e.getMessage());
                }
            }
        });
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void rangeCallbackEvent(EntityDamageByEntityEvent event) {

        if (FAKED_EVENTS.contains(event)) {
            return;
        }
        if (event.getEntity() == null
                || event.getDamager() == null
                || !(event.getEntity() instanceof LivingEntity)
                || event.getEntity().hasMetadata("NPC")
                || event.getDamager().hasMetadata("NPC")) {
            return;
        }
        boolean callback = false;
        // check if the entity was damaged by a projectile
        if ((event.getDamager() instanceof Projectile)) {

            ProjectileSource shooter = ((Projectile) event.getDamager()).getShooter();
            if (shooter == null || !(shooter instanceof LivingEntity)) {
                return;
            }
            CharacterTemplate source = plugin.getCharacterManager().getCharacter((LivingEntity) shooter);
            CharacterTemplate target = plugin.getCharacterManager().getCharacter((LivingEntity) event.getEntity());
            try {
                // lets check a fake damage event
                EntityDamageByEntityEvent fakeEvent = fakeDamageEvent(source, target);
                if (fakeEvent.isCancelled() || source.isFriendly(target)) {
                    event.setCancelled(true);
                    return;
                }
                // lets check pvp flags first
                checkPvPAttack(source, target);
                // and go thru all registered callbacks
                for (SourcedRangeCallback<RangedCallback> sourcedCallback : new ArrayList<>(entityHitCallbacks.values())) {
                    if (sourcedCallback.getProjectile().equals(event.getDamager()) && sourcedCallback.getSource().equals(source)) {
                        // cancel the damage event because we are handling it quit well :)
                        event.setCancelled(true);
                        try {
                            // lets damage the target with the ranged attack
                            target.damage(sourcedCallback.getAttack());
                            if (sourcedCallback.getCallback() != null) {
                                // the shooter is our source so lets call back and remove
                                sourcedCallback.getCallback().run(target);
                            }
                            entityHitCallbacks.remove(sourcedCallback.getTaskId());
                            if (sourcedCallback.getSource() instanceof Hero) {
                                ((Hero) sourcedCallback.getSource()).debug("Called Range Entity Callback - " + sourcedCallback.getTaskId());
                            }
                        } catch (CombatException e) {
                            if (sourcedCallback.getSource() instanceof Hero) {
                                ((Hero) sourcedCallback.getSource()).sendMessage(ChatColor.RED + e.getMessage());
                            }
                        }
                        callback = true;
                    }
                }
                if (!callback) {
                    boolean damaged = false;
                    // lets check all registered ranged attacks first
                    for (SourcedRangeCallback attack : new ArrayList<>(rangedAttacks.values())) {
                        if (attack.getProjectile().equals(event.getDamager()) && attack.getSource().equals(source)) {
                            target.damage(attack.getAttack());
                            rangedAttacks.remove(attack.getTaskId());
                            damaged = true;
                            break;
                        }
                    }
                    if (!damaged) {
                        boolean knockback = true;
                        // nerf skeletons
                        if (plugin.getCommonConfig().skeletons_knockback_chance < 1.0
                                && source.getEntity() instanceof Skeleton) {
                            if (Math.random() > plugin.getCommonConfig().skeletons_knockback_chance) {
                                knockback = false;
                            }
                        }
                        // lets issue a new physical attack for the event
                        try {
                            PhysicalAttack attack = new PhysicalAttack(source, target, source.getDamage());
                            attack.addWeapons(source.getWeapons());
                            attack.addAttackTypes(EffectType.DEFAULT_ATTACK);
                            attack.setKnockback(knockback);
                            attack.run();
                        } catch (CombatException e) {
                            event.setCancelled(true);
                            if (source instanceof Hero) {
                                ((Hero) source).sendMessage(ChatColor.RED + e.getMessage());
                            }
                        }
                    }
                }
                event.getDamager().remove();
                event.setCancelled(true);
            } catch (CombatException e) {
                if (source instanceof Hero) {
                    ((Hero) source).sendMessage(ChatColor.RED + e.getMessage());
                }
                event.setCancelled(true);
            }
        }
    }

    public static EntityDamageByEntityEvent fakeDamageEvent(CharacterTemplate attacker, CharacterTemplate victim) {

        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(
                attacker.getEntity(),
                victim.getEntity(),
                EntityDamageEvent.DamageCause.ENTITY_ATTACK,
                1
        );
        // we need to check for our own faked events to avoid loops
        FAKED_EVENTS.add(event);
        RaidCraft.callEvent(event);
        FAKED_EVENTS.remove(event);
        return event;
    }
}
