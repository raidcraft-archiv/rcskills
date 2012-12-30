package de.raidcraft.skills.api.trigger;

import de.raidcraft.skills.api.exceptions.CombatException;
import org.bukkit.event.EventException;

/**
 * @author Silthus
 */
public interface TriggerExecutor {

    public void execute(Triggered listener, Trigger trigger) throws CombatException, EventException;
}
