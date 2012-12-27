package de.raidcraft.skills;

import de.raidcraft.skills.api.trigger.TriggerManager;
import de.raidcraft.skills.trigger.BlockBreakTrigger;
import de.raidcraft.skills.trigger.BlockPlaceTrigger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * @author Silthus
 */
public class BukkitEventDispatcher implements Listener {

    private final SkillsPlugin plugin;

    public BukkitEventDispatcher(SkillsPlugin plugin) {

        this.plugin = plugin;
        plugin.registerEvents(this);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {

        TriggerManager.callTrigger(
                new BlockBreakTrigger(plugin.getCharacterManager().getHero(event.getPlayer()), event)
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {

        TriggerManager.callTrigger(
                new BlockPlaceTrigger(plugin.getCharacterManager().getHero(event.getPlayer()), event)
        );
    }
}
