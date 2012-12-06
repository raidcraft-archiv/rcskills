package de.raidcraft.skills.api.combat;

import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.*;

/**
 * @author Silthus
 */
public final class CombatManager implements Listener {

    private final SkillsPlugin plugin;

    private final Map<LivingEntity, Set<Effect>> appliedEffects = new HashMap<>();
    private final List<SourcedCallback> rangeCallbacks = new ArrayList<>();

    public CombatManager(SkillsPlugin plugin) {

        this.plugin = plugin;
        plugin.registerEvents(this);
    }

    public void addEffect(final Effect effect, final Hero source, final LivingEntity target) {

        if (!appliedEffects.containsKey(target)) {
            appliedEffects.put(target, new HashSet<Effect>());
        }
        final Set<Effect> effects = appliedEffects.get(target);
        // check for already existing effects of the same type
        if (effects.contains(effect)) {
            // lets cancel the old effect first
            for (Effect e : effects) {
                if (e.equals(effect)) {
                    // lets check if the effect is stronger and if yes cancel the old one
                    if (effect.getStrength() > e.getStrength()) {
                        Bukkit.getScheduler().cancelTask(e.getTaskId());
                    } else {
                        // tell the hero that a stronger effect of the same type is active
                        source.sendMessage(ChatColor.RED + "Es ist bereits ein stärkerer Effect vom selben Typ aktiv.");
                        return;
                    }
                }
            }
        }
        // apply the effect to the target and start the scheduler
        effect.setTaskId(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {

                try {
                    effect.apply(source, target);
                } catch (CombatException e) {
                    // TODO: catch exception
                }
            }
        }, effect.getDelay(), effect.getInterval()));
        // start the cancel task if the duration is > -1
        if (effect.getDuration() > -1) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {

                    Bukkit.getScheduler().cancelTask(effect.getTaskId());
                    effects.remove(effect);
                }
                // we choose this values because we want to cancel after the effect ticked at least once
            }, effect.getDuration() + effect.getDelay() + effect.getInterval());
        }
        // add the new effect to our applied list
        effects.add(effect);
    }

    public void damageEntity(LivingEntity source, LivingEntity target, int damage) throws CombatException {

        // create a fake event to make sure the damage is not cancelled
        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(source, target, EntityDamageEvent.DamageCause.CUSTOM, 0);
        if (event.isCancelled()) {
            throw new CombatException("Damage Event was cancelled.", CombatException.FailCause.CANCELLED);
        }
        // damage the actual entity
        target.setNoDamageTicks(0);
        target.setLastDamage(damage);
        int newHealth = target.getHealth() - damage;
        if (newHealth < 0) {
            newHealth = 0;
        }
        target.setHealth(newHealth);
        // TODO: play death animation
        target.setLastDamageCause(event);
        // TODO: check if it actually works like this
    }

    public void damageEntity(LivingEntity source, LivingEntity target, int damage, Callback callback) throws CombatException {

        damageEntity(source, target, damage);

        if (target == null || target.isDead()) {
            return;
        }
        // we need to check if it is a projectile or not
        if (callback instanceof RangedCallback) {
            castRangeAttack(source, callback);
        } else {
            // lets call it directly
            callback.run(target);
        }
    }

    public void castRangeAttack(LivingEntity source, Callback callback) {

        // lets add it to the listener
        final SourcedCallback cb = new SourcedCallback(source, callback);
        rangeCallbacks.add(cb);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {

                rangeCallbacks.remove(cb);
            }
        }, plugin.getCommonConfig().callback_purge_time);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent event) {

        // we need to remove entites that died from the effect list
        if (appliedEffects.containsKey(event.getEntity())) {
            appliedEffects.remove(event.getEntity());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

        Entity entity = event.getEntity();
        if (!(entity instanceof LivingEntity)) {
            return;
        }
        // check if the entity was damaged by a projectile
        if (!(event.getDamager() instanceof Projectile)) {
            return;
        }
        // and go thru all registered callbacks
        for (SourcedCallback sourcedCallback : new ArrayList<>(rangeCallbacks)) {
            if (sourcedCallback.getSource().equals(((Projectile) event.getDamager()).getShooter())) {
                // the shooter is our source so lets call back and remove
                sourcedCallback.getCallback().run((LivingEntity) entity);
                rangeCallbacks.remove(sourcedCallback);
            }
        }
    }
}
