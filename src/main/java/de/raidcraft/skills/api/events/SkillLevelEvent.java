package de.raidcraft.skills.api.events;

import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.skills.api.LevelableSkill;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Silthus
 */
public class SkillLevelEvent extends Event implements Cancellable {

    private final LevelableSkill skill;
    private final int nextLevel;
    private boolean cancelled = false;

    public SkillLevelEvent(LevelableSkill skill, int nextLevel) {

        this.skill = skill;
        this.nextLevel = nextLevel;
    }

    public LevelableSkill getSkill() {

        return skill;
    }

    public RCPlayer getPlayer() {

        return skill.getPlayer();
    }

    public int getNextLevel() {

        return nextLevel;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
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
