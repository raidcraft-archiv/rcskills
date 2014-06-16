package de.raidcraft.skills.api.events;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.hero.Hero;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * @author Silthus
 */
public class RCHeroLevelEvent extends RCLevelEvent<CharacterTemplate> implements Cancellable {


    private static final HandlerList handlers = new HandlerList();

    /*///////////////////////////////////////////////////
    //              Needed Bukkit Stuff
    ///////////////////////////////////////////////////*/

    public RCHeroLevelEvent(Hero source, int oldLevel, int newLevel) {

        super(source, oldLevel, newLevel);
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }

    public HandlerList getHandlers() {

        return handlers;
    }
}
