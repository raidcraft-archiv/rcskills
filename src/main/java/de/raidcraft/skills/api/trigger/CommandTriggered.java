package de.raidcraft.skills.api.trigger;

import com.sk89q.minecraft.util.commands.CommandContext;
import de.raidcraft.skills.api.exceptions.CombatException;

/**
 * @author Silthus
 */
public interface CommandTriggered {

    void runCommand(CommandContext args) throws CombatException;
}
