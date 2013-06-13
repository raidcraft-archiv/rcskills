package de.raidcraft.skills;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.combat.ThreatTable;
import de.raidcraft.skills.api.combat.action.Attack;
import de.raidcraft.skills.api.combat.action.PhysicalAttack;
import de.raidcraft.skills.api.combat.callback.LocationCallback;
import de.raidcraft.skills.api.combat.callback.RangedCallback;
import de.raidcraft.skills.api.combat.callback.SourcedRangeCallback;
import de.raidcraft.skills.api.effect.common.Combat;
import de.raidcraft.skills.api.effect.common.QueuedAttack;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.trigger.TriggerHandler;
import de.raidcraft.skills.api.trigger.TriggerManager;
import de.raidcraft.skills.api.trigger.TriggerPriority;
import de.raidcraft.skills.api.trigger.Triggered;
import de.raidcraft.skills.trigger.AttackTrigger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Silthus
 */
public final class CombatManager implements Listener, Triggered {

    public static final Set<EntityDamageByEntityEvent> FAKED_EVENTS = new HashSet<>();

    public static EntityDamageByEntityEvent fakeDamageEvent(Attack<CharacterTemplate, CharacterTemplate> action) {

        return fakeDamageEvent(action.getSource(), action);
    }

    public static EntityDamageByEntityEvent fakeDamageEvent(CharacterTemplate attacker, Attack <?, CharacterTemplate> action) {

        if (action.isOfAttackType(EffectType.MAGICAL)) {
            return fakeDamageEvent(attacker, action, EntityDamageEvent.DamageCause.MAGIC);
        } else {
            return fakeDamageEvent(attacker, action, EntityDamageEvent.DamageCause.ENTITY_ATTACK);
        }
    }

    public static EntityDamageByEntityEvent fakeDamageEvent(CharacterTemplate attacker, Attack <?, CharacterTemplate> action, EntityDamageByEntityEvent.DamageCause cause) {

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
    private final SkillsPlugin plugin;
    private final Map<Integer, SourcedRangeCallback<RangedCallback>> entityHitCallbacks = new HashMap<>();
    private final Map<Integer, SourcedRangeCallback<LocationCallback>> locationCallbacks = new HashMap<>();

    private final Map<Integer, SourcedRangeCallback> rangedAttacks = new HashMap<>();

    protected CombatManager(SkillsPlugin plugin) {

        this.plugin = plugin;
        plugin.registerEvents(this);
        TriggerManager.registerListeners(this);
    }

    public void reload() {

        entityHitCallbacks.clear();
        locationCallbacks.clear();
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

    @TriggerHandler(ignoreCancelled = true, filterTargets = false, priority = TriggerPriority.LOWEST)
    public void onAttack(AttackTrigger trigger) throws CombatException {

        if (!(trigger.getAttack().getTarget() instanceof Hero) || !(trigger.getSource() instanceof Hero)) {
            return;
        }
        Hero victim = (Hero) trigger.getAttack().getTarget();
        Hero attacker = (Hero) trigger.getSource();
        if (victim.isPvPEnabled() && attacker.isPvPEnabled()) {
            return;
        }
        // lets check some advanced stuff first, like if the attacking player has pvp disabled and the victim has pvp enabled
        if (victim.isPvPEnabled() && !attacker.isPvPEnabled()) {
            attacker.setPvPEnabled(true);
            attacker.sendMessage(ChatColor.RED + "Dein PvP Status wurde auf aktiv gesetzt!");
        } else if (!victim.isPvPEnabled()) {
            trigger.setCancelled(true);
            trigger.getAttack().setCancelled(true);
            throw new CombatException("Dein Ziel hat PvP nicht aktiviert und kann nicht angegriffen werden!");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onTarget(EntityTargetLivingEntityEvent event) {

        if (!(event.getEntity() instanceof Creature)) {
            return;
        }

        CharacterTemplate creature = plugin.getCharacterManager().getCharacter((LivingEntity) event.getEntity());
        CharacterTemplate target = plugin.getCharacterManager().getCharacter(event.getTarget());

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

        if (attacker.isFriendly(victim)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
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
                    physicalAttack.addAttackTypes(EffectType.DEFAULT_ATTACK);
                }

                physicalAttack.setKnockback(true);
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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void enterCombat(EntityDamageByEntityEvent event) {

        if (FAKED_EVENTS.contains(event)) {
            return;
        }
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

        LivingEntity shooter = event.getEntity().getShooter();
        if (shooter == null) {
            return;
        }
        CharacterTemplate source = plugin.getCharacterManager().getCharacter(shooter);
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

        if (FAKED_EVENTS.contains(event)) {
            return;
        }
        if (event.getEntity() == null
                || event.getDamager() == null
                || !(event.getEntity() instanceof LivingEntity)) {
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
                        PhysicalAttack attack = new PhysicalAttack(source, target, source.getDamage(), EffectType.DEFAULT_ATTACK);
                        attack.setKnockback(knockback);
                        attack.run();
                    } catch (CombatException e) {
                        if (source instanceof Hero) {
                            ((Hero) source).sendMessage(ChatColor.RED + e.getMessage());
                        }
                    }
                }
                event.setCancelled(true);
            }
        }
    }
}
