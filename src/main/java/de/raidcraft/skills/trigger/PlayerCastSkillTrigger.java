package de.raidcraft.skills.trigger;

import de.raidcraft.skills.api.combat.action.SkillAction;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.trigger.HandlerList;
import de.raidcraft.skills.api.trigger.Trigger;
import org.bukkit.event.Cancellable;

/**
 * @author Silthus
 */
public class PlayerCastSkillTrigger extends Trigger implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final SkillAction action;
    private boolean cancelled = false;

    public PlayerCastSkillTrigger(SkillAction action) {

        super(action.getSource());
        this.action = action;
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }

    /*///////////////////////////////////////////////////
    //              Needed Trigger Stuff
    ///////////////////////////////////////////////////*/

    public Skill getSkill() {

        return action.getSkill();
    }

    public SkillAction getAction() {

        return action;
    }

    public HandlerList getHandlers() {

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
