package de.raidcraft.skills.api.combat.action;

/**
 * @author Silthus
 */
public abstract class AbstractAttack<S, T> extends AbstractAction<S> implements Attack<S, T> {

    private final T target;
    private int damage;
    private boolean cancelled = false;

    protected AbstractAttack(S attacker, T target, int damage) {

        super(attacker);
        this.target = target;
        this.damage = damage;
    }

    @Override
    public T getTarget() {

        return target;
    }

    @Override
    public int getDamage() {

        return damage;
    }

    @Override
    public void setDamage(int damage) {

        this.damage = damage;
    }

    @Override
    public void setCancelled(boolean cancelled) {

        this.cancelled = cancelled;
    }

    @Override
    public boolean isCancelled() {

        return cancelled;
    }
}
