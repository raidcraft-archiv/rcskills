package de.raidcraft.skills.trigger;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.effect.Effect;
import de.raidcraft.skills.api.trigger.HandlerList;
import de.raidcraft.skills.api.trigger.Trigger;
import org.bukkit.event.Cancellable;

/**
 * @author Silthus
 */
public class PlayerGainedEffectTrigger extends Trigger implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Effect<?> effect;
    private boolean cancelled = false;

    public PlayerGainedEffectTrigger(CharacterTemplate source, Effect<?> effect) {

        super(source);
        this.effect = effect;
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }

    public Effect<?> getEffect() {

        return effect;
    }

    /*///////////////////////////////////////////////////
    //              Needed Trigger Stuff
    ///////////////////////////////////////////////////*/

    @Override
    public boolean isCancelled() {

        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {

        this.cancelled = cancel;
    }

    public HandlerList getHandlers() {

        return handlers;
    }
}
