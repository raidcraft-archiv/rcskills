package de.raidcraft.skills.trigger;

import com.sk89q.minecraft.util.commands.CommandContext;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.trigger.HandlerList;
import de.raidcraft.skills.api.trigger.Trigger;

/**
 * @author Silthus
 */
public class CommandTrigger extends Trigger {

    private final CommandContext commandContext;

    public CommandTrigger(Hero hero, CommandContext commandContext) {

        super(hero);
        this.commandContext = commandContext;
    }

    public CommandContext getCommandContext() {

        return commandContext;
    }

    /*///////////////////////////////////////////////////
    //              Needed Trigger Stuff
    ///////////////////////////////////////////////////*/

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
