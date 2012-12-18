package de.raidcraft.skills.api.combat.attack;

/**
 * @author Silthus
 */
public abstract class AbstractAttack<S, T> extends AbstractAction<S> implements Attack<S, T> {

    private final T target;
    private int damage;

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
}
