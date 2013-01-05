package de.raidcraft.skills.trigger;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.action.Attack;
import de.raidcraft.skills.api.trigger.HandlerList;
import de.raidcraft.skills.api.trigger.Trigger;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * @author Silthus
 */
public class DamageTrigger extends Trigger {

    private final Attack<?, CharacterTemplate> attack;
    private final EntityDamageEvent.DamageCause cause;

    public DamageTrigger(CharacterTemplate source, Attack<?, CharacterTemplate> attack, EntityDamageEvent.DamageCause cause) {

        super(source);
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
}
