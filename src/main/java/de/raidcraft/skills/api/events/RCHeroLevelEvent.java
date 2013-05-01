package de.raidcraft.skills.api.events;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.hero.Hero;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * @author Silthus
 */
public class RCHeroLevelEvent extends RCLevelEvent<CharacterTemplate> implements Cancellable {


    public RCHeroLevelEvent(Hero source, int oldLevel, int newLevel) {

        super(source, oldLevel, newLevel);
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
