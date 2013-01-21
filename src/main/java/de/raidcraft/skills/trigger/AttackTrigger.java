package de.raidcraft.skills.trigger;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.action.Attack;
import de.raidcraft.skills.api.trigger.HandlerList;
import de.raidcraft.skills.api.trigger.Trigger;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * @author Silthus
 */
public class AttackTrigger extends Trigger implements Cancellable {

    private final Attack<?, CharacterTemplate> attack;
    private final EntityDamageEvent.DamageCause cause;
    private boolean cancelled = false;

    public AttackTrigger(CharacterTemplate hero, Attack<?, CharacterTemplate> attack, EntityDamageByEntityEvent.DamageCause cause) {

        super(hero);
        this.attack = attack;
        this.cause = cause;
    }

    public Attack<?, CharacterTemplate> getAttack() {

        return attack;
    }

    public EntityDamageEvent.DamageCause getCause() {

        return cause;
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
