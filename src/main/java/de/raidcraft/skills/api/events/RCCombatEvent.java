package de.raidcraft.skills.api.events;

import de.raidcraft.skills.api.hero.Hero;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Silthus
 */
public class RCCombatEvent extends Event {

    public enum Type {

        LEAVE,
        ENTER
    }

    private final Hero hero;
    private final Type type;

    public RCCombatEvent(Hero hero, Type type) {

        this.hero = hero;
        this.type = type;
    }

    public Hero getHero() {

        return hero;
    }

    public Type getType() {

        return type;
    }

    /*///////////////////////////////////////////////////
    //              Needed Bukkit Stuff
    ///////////////////////////////////////////////////*/

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {

        return handlers;
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }
}
