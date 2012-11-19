package de.raidcraft.skills.api.bukkit;

import com.sk89q.commandbook.CommandBook;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import de.raidcraft.RaidCraft;
import de.raidcraft.skills.RCHero;
import de.raidcraft.skills.SkillsComponent;
import de.raidcraft.skills.api.Active;
import de.raidcraft.skills.api.Passive;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.trigger.Trigger;
import de.raidcraft.skills.trigger.BlockTrigger;
import de.raidcraft.skills.trigger.InteractTrigger;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Silthus
 */
public final class BukkitListenerAdapter implements Listener {

    private final SkillsComponent component;

    public BukkitListenerAdapter(SkillsComponent component) {

        this.component = component;
    }

    @SuppressWarnings("unchecked")
    private void runTask(final Trigger trigger) {

        RCHero player = trigger.getPlayer().getComponent(RCHero.class);

        final Collection<Skill> skills = new ArrayList<>(player.getSkills());

        Bukkit.getScheduler().scheduleAsyncDelayedTask(CommandBook.inst(), new Runnable() {
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
        });
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
