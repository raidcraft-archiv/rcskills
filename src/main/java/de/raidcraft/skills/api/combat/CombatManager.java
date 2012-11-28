package de.raidcraft.skills.api.combat;

import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Silthus
 */
public final class CombatManager implements Listener {

    private final SkillsPlugin plugin;

    private final Map<LivingEntity, Set<Effect>> appliedEffects = new HashMap<>();

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
                    Bukkit.getScheduler().cancelTask(e.getTaskId());
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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent event) {

        // we need to remove entites that died from the effect list
        if (appliedEffects.containsKey(event.getEntity())) {
            appliedEffects.remove(event.getEntity());
        }
    }
}
