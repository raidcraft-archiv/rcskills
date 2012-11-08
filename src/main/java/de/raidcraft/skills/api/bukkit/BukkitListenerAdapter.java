package de.raidcraft.skills.api.bukkit;

import com.sk89q.worldedit.bukkit.BukkitUtil;
import de.raidcraft.rcrpg.RaidCraft;
import de.raidcraft.skills.SkilledPlayer;
import de.raidcraft.skills.SkillsComponent;
import de.raidcraft.skills.api.Active;
import de.raidcraft.skills.api.Passive;
import de.raidcraft.skills.api.Skill;
import de.raidcraft.skills.api.trigger.Trigger;
import de.raidcraft.skills.trigger.BlockTrigger;
import de.raidcraft.skills.trigger.InteractTrigger;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Silthus
 */
public final class BukkitListenerAdapter implements Listener {

    private final SkillsComponent component;
    private final ThreadPoolExecutor threadPool;

    public BukkitListenerAdapter(SkillsComponent component) {

        this.component = component;
        SkillsComponent.LocalConfiguration config = component.getLocalConfiguration();
        // generate our thread pool
        this.threadPool = new ThreadPoolExecutor(
                config.maxCoreSize, config.maxPoolSize, config.keepAlive, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(5));
    }

    @SuppressWarnings("unchecked")
    private void runTask(final Trigger trigger) {

        SkilledPlayer player = trigger.getPlayer().getComponent(SkilledPlayer.class);

        final Collection<Skill> skills = Collections.unmodifiableCollection(player.getSkills());

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // we need a try block for every possiblity because a skill might listen on passive and active triggers
                for (Skill skill : skills) {
                    try {
                        if (skill instanceof Active) {
                            ((Active) skill).run(trigger);
                        }
                    } catch (ClassCastException ignored) {
                        // this gets ignored because we now know it is not listening on this trigger
                    }
                    try {
                        if (skill instanceof Passive) {
                            ((Passive) skill).apply(trigger);
                        }
                    } catch (ClassCastException ignored) {
                        // this gets ignored because we now know it is not listening on this trigger
                    }
                }
            }
        };

        threadPool.execute(runnable);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {

        Block block = event.getBlock();
        runTask(new BlockTrigger(
                BukkitUtil.toWorldVector(block),
                block.getTypeId(),
                block.getData(),
                RaidCraft.getPlayer(event.getPlayer()),
                BlockTrigger.Action.BREAK));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {

        Block block = event.getClickedBlock();
        runTask(new InteractTrigger(
                BukkitUtil.toWorldVector(block),
                block.getTypeId(),
                block.getData(),
                RaidCraft.getPlayer(event.getPlayer()),
                event.getAction()));
    }
}
