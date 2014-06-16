package de.raidcraft.skills.trigger;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.trigger.BukkitEventTrigger;
import de.raidcraft.skills.api.trigger.HandlerList;
import org.bukkit.event.entity.ProjectileHitEvent;

/**
 * @author Silthus
 */
public class ProjectileHitTrigger extends BukkitEventTrigger<ProjectileHitEvent> {

    private static final HandlerList handlers = new HandlerList();

    /*///////////////////////////////////////////////////
    //              Needed Trigger Stuff
    ///////////////////////////////////////////////////*/

    public ProjectileHitTrigger(CharacterTemplate source, ProjectileHitEvent event) {

        super(source, event);
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }

    public HandlerList getHandlers() {

        return handlers;
    }
}
