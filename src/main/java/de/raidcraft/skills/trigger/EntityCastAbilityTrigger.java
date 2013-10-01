package de.raidcraft.skills.trigger;

import de.raidcraft.skills.api.ability.Ability;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.action.AbilityAction;
import de.raidcraft.skills.api.trigger.HandlerList;
import de.raidcraft.skills.api.trigger.Trigger;
import org.bukkit.event.Cancellable;

/**
 * @author Silthus
 */
public class EntityCastAbilityTrigger extends Trigger implements Cancellable {

    private final AbilityAction<? extends CharacterTemplate> action;
    private boolean cancelled = false;

    public EntityCastAbilityTrigger(AbilityAction<? extends CharacterTemplate> action) {

        super(action.getSource());
        this.action = action;
    }

    public Ability<? extends CharacterTemplate> getAbility() {

        return action.getAbility();
    }

    public AbilityAction<? extends CharacterTemplate> getAction() {

        return action;
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

    @Override
    public boolean isCancelled() {

        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {

        this.cancelled = b;
    }
}
