package de.raidcraft.skills.api.combat;

import de.raidcraft.skills.SkillsPlugin;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Map;

/**
 * @author Silthus
 */
public final class CombatManager implements Listener {

    private final SkillsPlugin plugin;

    private final Map<LivingEntity, List<Effect>>

    public CombatManager(SkillsPlugin plugin) {

        this.plugin = plugin;
    }
}
