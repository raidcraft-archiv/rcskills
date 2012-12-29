package de.raidcraft.skills;

import de.raidcraft.api.InvalidTargetException;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.action.PhysicalAttack;
import de.raidcraft.skills.api.combat.callback.SourcedRangeCallback;
import de.raidcraft.skills.api.effect.common.CastTime;
import de.raidcraft.skills.api.effect.common.Combat;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public final class CombatManager implements Listener {

    private final SkillsPlugin plugin;
    private final Map<Integer, SourcedRangeCallback> callbacks = new HashMap<>();

    protected CombatManager(SkillsPlugin plugin) {

        this.plugin = plugin;
        plugin.registerEvents(this);
    }

    public void reload() {

        // dont clear the callbacks let them run out quietly to not interrupt combat
    }

    public void queueCallback(final SourcedRangeCallback sourcedCallback) {

        // remove the callback from the queue after the configured time
        sourcedCallback.setTaskId(Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {

                callbacks.remove(sourcedCallback.getTaskId());
            }
        }, plugin.getCommonConfig().callback_purge_time));
        callbacks.put(sourcedCallback.getTaskId(), sourcedCallback);
        if (sourcedCallback.getSource() instanceof Hero) {
            ((Hero) sourcedCallback.getSource()).debug("Queued Range Callback - " + sourcedCallback.getTaskId());
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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void enterCombat(EntityDamageByEntityEvent event) {

        if (event.getEntity() instanceof LivingEntity) {
            CharacterTemplate victim = plugin.getCharacterManager().getCharacter((LivingEntity) event.getEntity());
            CharacterTemplate attacker;
            if (event.getDamager() instanceof LivingEntity) {
                attacker = plugin.getCharacterManager().getCharacter((LivingEntity) event.getDamager());
            } else if (event.getDamager() instanceof Projectile) {
                attacker = plugin.getCharacterManager().getCharacter(((Projectile) event.getDamager()).getShooter());
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
            for (SourcedRangeCallback sourcedCallback : new ArrayList<>(callbacks.values())) {
                if (sourcedCallback.getProjectile().equals(event.getDamager()) && sourcedCallback.getSource().equals(source)) {
                    // lets set the damage of the event to 0 because it is handled by us
                    event.setDamage(0);
                    try {
                        // the shooter is our source so lets call back and remove
                        sourcedCallback.getCallback().run(target);
                        callbacks.remove(sourcedCallback.getTaskId());
                        if (sourcedCallback.getSource() instanceof Hero) {
                            ((Hero) sourcedCallback.getSource()).debug("Called Range Callback - " + sourcedCallback.getTaskId());
                        }
                    } catch (CombatException | InvalidTargetException e) {
                        if (sourcedCallback.getSource() instanceof Hero) {
                            ((Hero) sourcedCallback.getSource()).sendMessage(ChatColor.RED + e.getMessage());
                        }
                    }
                    callback = true;
                }
            }
            if (!callback) {
                // lets issue a new physical attack for the event
                try {
                    new PhysicalAttack(source, target, event.getDamage()).run();
                } catch (CombatException ignored) {
                }
                event.setDamage(0);
            }
        }
    }
}
