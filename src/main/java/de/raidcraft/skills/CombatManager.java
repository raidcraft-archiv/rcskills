package de.raidcraft.skills;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.combat.action.PhysicalAttack;
import de.raidcraft.skills.api.combat.action.WeaponAttack;
import de.raidcraft.skills.api.combat.callback.LocationCallback;
import de.raidcraft.skills.api.combat.callback.RangedCallback;
import de.raidcraft.skills.api.combat.callback.SourcedRangeCallback;
import de.raidcraft.skills.api.effect.common.CastTime;
import de.raidcraft.skills.api.effect.common.Combat;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.items.Weapon;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Silthus
 */
public final class CombatManager implements Listener {

    private final SkillsPlugin plugin;
    private final Map<Integer, SourcedRangeCallback<RangedCallback>> entityHitCallbacks = new HashMap<>();
    private final Map<Integer, SourcedRangeCallback<LocationCallback>> locationCallbacks = new HashMap<>();
    private final Map<Integer, SourcedRangeCallback> rangedAttacks = new HashMap<>();

    protected CombatManager(SkillsPlugin plugin) {

        this.plugin = plugin;
        plugin.registerEvents(this);
    }

    public void reload() {

        // dont clear the callbacks let them run out quietly to not interrupt combat
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

                entityHitCallbacks.remove(sourcedCallback.getTaskId());
            }
        }, plugin.getCommonConfig().callback_purge_time));
        locationCallbacks.put(sourcedCallback.getTaskId(), sourcedCallback);
        if (sourcedCallback.getSource() instanceof Hero) {
            ((Hero) sourcedCallback.getSource()).debug("Queued Range Location Callback - " + sourcedCallback.getTaskId());
        }
    }

    /*//////////////////////////////////////////////////////////////////////////////
    //      Hooked Bukkit events for handling all the combat stuff
    //////////////////////////////////////////////////////////////////////////////*/

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onInteract(PlayerInteractEvent event) {

        try {
            plugin.getCharacterManager().getHero(event.getPlayer()).removeEffect(CastTime.class);
        } catch (CombatException e) {
            event.getPlayer().sendMessage(ChatColor.RED + e.getMessage());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onTarget(EntityTargetLivingEntityEvent event) {

        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        CharacterTemplate creature = plugin.getCharacterManager().getCharacter((LivingEntity) event.getEntity());
        CharacterTemplate target = plugin.getCharacterManager().getCharacter(event.getTarget());

        if (target.isFriendly(creature)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onFriendlyAttack(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof LivingEntity) || !(event.getDamager() instanceof LivingEntity)) {
            return;
        }

        CharacterTemplate victim = plugin.getCharacterManager().getCharacter((LivingEntity) event.getEntity());
        CharacterTemplate attacker = plugin.getCharacterManager().getCharacter((LivingEntity) event.getDamager());

        if (attacker.isFriendly(victim)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onAttack(EntityDamageByEntityEvent event) {

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

        CharacterTemplate target = plugin.getCharacterManager().getCharacter((LivingEntity) event.getEntity());
        CharacterTemplate attacker = null;
        if (event.getDamager() instanceof LivingEntity) {
            attacker = plugin.getCharacterManager().getCharacter((LivingEntity) event.getDamager());
        }

        if (attacker == null) {
            return;
        }

        try {
            if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                // fisting is defined in the common config
                ItemStack itemInHand = attacker.getEntity().getEquipment().getItemInHand();
                if (attacker instanceof Hero && (itemInHand == null || itemInHand.getTypeId() == 0)) {
                    int damage = plugin.getCommonConfig().fist_attack_damage;
                    event.setCancelled(true);
                    new PhysicalAttack(attacker, target, damage, EffectType.DEFAULT_ATTACK).run();
                    return;
                }
                if (attacker instanceof Hero) {
                    // lets check all weapon slots of the char
                    Set<Weapon.Slot> attackingWeapons = new HashSet<>();

                    for (Weapon.Slot slot : Weapon.Slot.values()) {
                        if (attacker.hasWeapon(slot) && attacker.canSwing(slot)) {
                            attackingWeapons.add(slot);
                        }
                    }
                    if (attackingWeapons.size() < 1) {
                        event.setCancelled(true);
                        return;
                    }
                    // now lets issue an attack for each weapon
                    for (Weapon.Slot slot : attackingWeapons) {
                        Weapon weapon = attacker.getWeapon(slot);
                        WeaponAttack attack = new WeaponAttack(event, weapon, weapon.getDamage());
                        attack.addAttackTypes(EffectType.DEFAULT_ATTACK);
                        attack.run();
                        attacker.setLastSwing(slot);
                    }
                } else {
                    // this is for entity attacks
                    PhysicalAttack attack = new PhysicalAttack(event, attacker.getDamage());
                    attack.addAttackTypes(EffectType.DEFAULT_ATTACK);
                    // cancel event because we are handling stuff
                    attack.run();
                }
                event.setDamage(0);
            }
        } catch (CombatException e) {
            if (attacker instanceof Hero) {
                ((Hero) attacker).sendMessage(ChatColor.RED + e.getMessage());
            }
            if (target instanceof Hero) {
                ((Hero) target).debug((attacker.getName()) + "->You: " + e.getMessage());
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void enterCombat(EntityDamageByEntityEvent event) {

        if (event.getEntity() instanceof LivingEntity) {
            CharacterTemplate victim = plugin.getCharacterManager().getCharacter((LivingEntity) event.getEntity());
            CharacterTemplate attacker;
            if (event.getDamager() instanceof LivingEntity) {
                attacker = plugin.getCharacterManager().getCharacter((LivingEntity) event.getDamager());
            } else if (event.getDamager() instanceof Projectile) {
                LivingEntity shooter = ((Projectile) event.getDamager()).getShooter();
                if (shooter == null) return;
                attacker = plugin.getCharacterManager().getCharacter(shooter);
            } else {
                // no combat event
                return;
            }
            try {
                // add the combat effect to the attacker and victim
                victim.addEffect(attacker, Combat.class);
                attacker.addEffect(victim, Combat.class);
            } catch (CombatException e) {
                e.printStackTrace();
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void projectileHitEvent(ProjectileHitEvent event) {

        CharacterTemplate source = plugin.getCharacterManager().getCharacter(event.getEntity().getShooter());
        try {
            // lets add a combat effect first
            source.addEffect(source, Combat.class);
            // iterate over our queued callbacks
            for (SourcedRangeCallback<LocationCallback> sourcedCallback : new ArrayList<>(locationCallbacks.values())) {
                if (sourcedCallback.getProjectile().equals(event.getEntity()) && sourcedCallback.getSource().equals(source)) {
                    locationCallbacks.remove(sourcedCallback.getTaskId());
                    if (sourcedCallback.getSource() instanceof Hero) {
                        ((Hero) sourcedCallback.getSource()).debug("Called Range Location Callback - " + sourcedCallback.getTaskId());
                    }
                }
            }
        } catch (CombatException e) {
            if (source instanceof Hero) {
                ((Hero) source).sendMessage(ChatColor.RED + e.getMessage());
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void rangeCallbackEvent(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }
        boolean callback = false;
        // lets enter combat for that player
        enterCombat(event);
        // check if the entity was damaged by a projectile
        if ((event.getDamager() instanceof Projectile)) {
            CharacterTemplate source = plugin.getCharacterManager().getCharacter(((Projectile) event.getDamager()).getShooter());
            CharacterTemplate target = plugin.getCharacterManager().getCharacter((LivingEntity) event.getEntity());
            // and go thru all registered callbacks
            for (SourcedRangeCallback<RangedCallback> sourcedCallback : new ArrayList<>(entityHitCallbacks.values())) {
                if (sourcedCallback.getProjectile().equals(event.getDamager()) && sourcedCallback.getSource().equals(source)) {
                    // lets set the damage of the event to 0 because it is handled by us
                    event.setDamage(0);
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
                    // lets issue a new physical attack for the event
                    try {
                        new PhysicalAttack(source, target, source.getDamage()).run();
                    } catch (CombatException ignored) {
                    }
                }
                event.setDamage(0);
            }
        }
    }
}
