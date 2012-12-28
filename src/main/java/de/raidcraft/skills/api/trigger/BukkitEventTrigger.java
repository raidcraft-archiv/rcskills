package de.raidcraft.skills.api.trigger;

import de.raidcraft.skills.api.hero.Hero;
import org.bukkit.event.Event;

/**
 * @author Silthus
 */
public abstract class BukkitEventTrigger<E extends Event> extends Trigger {

    private final E event;

    public BukkitEventTrigger(Hero hero, E event) {

        super(hero);
        this.event = event;
    }

    public E getEvent() {

        return event;
    }
}
