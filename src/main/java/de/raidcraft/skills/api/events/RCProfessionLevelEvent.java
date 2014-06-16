package de.raidcraft.skills.api.events;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * @author Silthus
 */
public class RCProfessionLevelEvent extends RCLevelEvent<Profession> implements Cancellable {


    private static final HandlerList handlers = new HandlerList();

    public RCProfessionLevelEvent(Profession source, int oldLevel, int newLevel) {

        super(source, oldLevel, newLevel);
    }

    /*///////////////////////////////////////////////////
    //              Needed Bukkit Stuff
    ///////////////////////////////////////////////////*/

    public static HandlerList getHandlerList() {

        return handlers;
    }

    public Hero getHero() {

        return getSource().getHero();
    }

    public HandlerList getHandlers() {

        return handlers;
    }
}
