package de.raidcraft.skills.api.events;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.LevelableSkill;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * @author Silthus
 */
public class RCSkillLevelEvent extends RCLevelEvent<LevelableSkill> implements Cancellable {


    private static final HandlerList handlers = new HandlerList();

    public RCSkillLevelEvent(LevelableSkill source, int oldLevel, int newLevel) {

        super(source, oldLevel, newLevel);
    }

    /*///////////////////////////////////////////////////
    //              Needed Bukkit Stuff
    ///////////////////////////////////////////////////*/

    public static HandlerList getHandlerList() {

        return handlers;
    }

    public Hero getHero() {

        return getSource().getHolder();
    }

    public HandlerList getHandlers() {

        return handlers;
    }
}
