package de.raidcraft.skills.trigger;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.action.Attack;
import de.raidcraft.skills.api.trigger.HandlerList;
import de.raidcraft.skills.api.trigger.Trigger;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * @author Silthus
 */
public class DamageTrigger extends Trigger implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Attack<?, CharacterTemplate> attack;
    private final EntityDamageEvent.DamageCause cause;
    private boolean cancelled = false;

    public DamageTrigger(CharacterTemplate source, Attack<?, CharacterTemplate> attack, EntityDamageEvent.DamageCause cause) {

        super(source);
        this.attack = attack;
        this.cause = cause;
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }

    /*///////////////////////////////////////////////////
    //              Needed Trigger Stuff
    ///////////////////////////////////////////////////*/

    public Attack<?, CharacterTemplate> getAttack() {

        return attack;
    }

    public EntityDamageEvent.DamageCause getCause() {

        return cause;
    }

    public HandlerList getHandlers() {

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
