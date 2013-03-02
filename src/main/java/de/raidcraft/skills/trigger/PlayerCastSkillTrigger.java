package de.raidcraft.skills.trigger;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.trigger.HandlerList;
import de.raidcraft.skills.api.trigger.Trigger;
import org.bukkit.event.Cancellable;

/**
 * @author Silthus
 */
public class PlayerCastSkillTrigger extends Trigger implements Cancellable {

    private final Skill skill;
    private boolean cancelled = false;
    private long castTime;

    public PlayerCastSkillTrigger(CharacterTemplate source, Skill skill) {

        super(source);
        this.skill = skill;
        castTime = skill.getTotalCastTime();
    }

    public Skill getSkill() {

        return skill;
    }

    public long getCastTime() {

        return castTime;
    }

    public void setCastTime(long time) {

        this.castTime = time;
    }

    public boolean isCastTimeChanged() {

        return castTime != getSkill().getTotalCastTime();
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

    @Override
    public boolean isCancelled() {

        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {

        this.cancelled = b;
    }
}
