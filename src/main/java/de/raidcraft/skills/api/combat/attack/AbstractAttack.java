package de.raidcraft.skills.api.combat.attack;

/**
 * @author Silthus
 */
public abstract class AbstractAttack<A, T> implements Attack {

    private final A attacker;
    private final T target;
    private final int damage;

    protected AbstractAttack(A attacker, T target, int damage) {

        this.attacker = attacker;
        this.target = target;
        this.damage = damage;
    }

    @Override
    public A getAttacker() {

        return attacker;
    }

    @Override
    public T getTarget() {

        return target;
    }

    @Override
    public int getDamage() {

        return damage;
    }
}
