package de.raidcraft.skills.api.combat;

import de.raidcraft.skills.SkillsPlugin;
import org.bukkit.event.Listener;

/**
 * @author Silthus
 */
public final class CombatManager implements Listener {

    private final SkillsPlugin plugin;

    public CombatManager(SkillsPlugin plugin) {

        this.plugin = plugin;
    }
}
