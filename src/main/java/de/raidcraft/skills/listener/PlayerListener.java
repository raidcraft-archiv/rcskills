package de.raidcraft.skills.listener;

import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.events.RCEntityDeathEvent;
import de.raidcraft.skills.api.hero.Hero;
import lombok.Data;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@Data
public class PlayerListener implements Listener {

    private final SkillsPlugin plugin;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDeath(RCEntityDeathEvent event) {

        if (event.getCharacter() instanceof Hero) {
            event.getCharacter().getKiller()
                    .ifPresent(killer -> ((Hero) event.getCharacter()).debug(killer.getName() + " hat dich getötet."));
        }
    }
}
