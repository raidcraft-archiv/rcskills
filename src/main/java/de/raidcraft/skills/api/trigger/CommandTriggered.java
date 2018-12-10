package de.raidcraft.skills.api.trigger;

import com.sk89q.minecraft.util.commands.CommandContext;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.skill.Active;

/**
 * @author Silthus
 */
public interface CommandTriggered extends Active {

    void runCommand(CommandContext args) throws CombatException;
}
