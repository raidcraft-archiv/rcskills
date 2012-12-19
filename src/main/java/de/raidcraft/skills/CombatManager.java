package de.raidcraft.skills;

import de.raidcraft.api.InvalidTargetException;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.callback.SourcedRangeCallback;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.util.Vector;

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
    public void onEntityDeath(EntityDeathEvent event) {

        // lets check for dying players and characters
        LivingEntity entity = event.getEntity();
        CharacterTemplate character = plugin.getCharacterManager().getCharacter(entity);
        if (entity.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            if (((EntityDamageByEntityEvent) entity.getLastDamageCause()).getDamager() instanceof LivingEntity) {
                character.kill(plugin.getCharacterManager().getCharacter(
                        (LivingEntity) ((EntityDamageByEntityEvent) entity.getLastDamageCause()).getDamager()));
            }
        } else {
            character.kill();
        }
        // lets remove that poor character from our cache... may he Rest in Peace :*(
        plugin.getCharacterManager().clearCacheOf(character);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }
        // check if the entity was damaged by a projectile
        if ((event.getDamager() instanceof Projectile)) {
            // and go thru all registered callbacks
            for (SourcedRangeCallback sourcedCallback : new ArrayList<>(callbacks.values())) {
                if (sourcedCallback.getProjectile().equals(event.getDamager())) {
                    try {
                        // the shooter is our source so lets call back and remove
                        sourcedCallback.getCallback().run(plugin.getCharacterManager().getCharacter((LivingEntity) event.getEntity()));
                        callbacks.remove(sourcedCallback.getTaskId());
                        if (sourcedCallback.getSource() instanceof Hero) {
                            ((Hero) sourcedCallback.getSource()).debug("Called Range Callback - " + sourcedCallback.getTaskId());
                        }
                    } catch (CombatException | InvalidTargetException e) {
                        if (sourcedCallback.getSource() instanceof Hero) {
                            ((Hero) sourcedCallback.getSource()).sendMessage(ChatColor.RED + e.getMessage());
                        }
                    }
                }
            }

        }
    }

    public void knockBack(CharacterTemplate attacker, LivingEntity target, double power) {

        // TODO: make effect out of this
        // knocks back the target based on the attackers center position
        Location knockBackCenter = attacker.getEntity().getLocation();
        double xOff = target.getLocation().getX() - knockBackCenter.getX();
        double yOff = target.getLocation().getY() - knockBackCenter.getY();
        double zOff = target.getLocation().getZ() - knockBackCenter.getZ();
        // power is the velocity applied to the target
        // a power of 0.4 is a player jumping
        target.setVelocity(new Vector(xOff, yOff, zOff).normalize().multiply(power));
    }
}
