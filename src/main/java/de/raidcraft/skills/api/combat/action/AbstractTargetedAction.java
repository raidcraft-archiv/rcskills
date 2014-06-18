package de.raidcraft.skills.api.combat.action;

import de.raidcraft.skills.api.hero.Hero;

/**
 * @author Silthus
 */
public abstract class AbstractTargetedAction<S, T> extends AbstractAction<S> implements TargetedAction<S, T> {

    protected final T target;
    private boolean cancelled = false;

    protected AbstractTargetedAction(S source, T target) {

        super(source);
        this.target = target;
    }

    public boolean isCancelled() {

        return cancelled;
    }    @Override
    public T getTarget() {

        return target;
    }

    public void setCancelled(boolean cancelled) {

        this.cancelled = cancelled;
    }



    @Override
    public void combatLog(Object o, String message) {

        message = message.replace("<t>", getTarget().toString()).replace("<s>", getSource().toString());
        if (getTarget() instanceof Hero) {
            ((Hero) getTarget()).combatLog(o, message);
        }
        if (getSource() instanceof Hero) {
            ((Hero) getSource()).combatLog(o, message);
        }
    }
}
