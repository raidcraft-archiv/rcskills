package de.raidcraft.skills.api.trigger;

import de.raidcraft.skills.api.character.CharacterTemplate;
import org.bukkit.event.Event;

/**
 * @author Silthus
 */
public abstract class BukkitEventTrigger<E extends Event> extends Trigger {

    private final E event;

    public BukkitEventTrigger(CharacterTemplate source, E event) {

        super(source);
        this.event = event;
    }

    public E getEvent() {

        return event;
    }
}
