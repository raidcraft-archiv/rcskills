package de.raidcraft.skills.api.combat.action;

import de.raidcraft.skills.api.combat.AttackSource;
import de.raidcraft.skills.api.combat.AttackType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Silthus
 */
public abstract class AbstractAttack<S, T> extends AbstractAction<S> implements Attack<S, T> {

    private final T target;
    private int damage;
    private boolean cancelled = false;
    private final Set<AttackType> attackTypes = new HashSet<>();
    private final AttackSource source;

    protected AbstractAttack(S attacker, T target, int damage, AttackType... types) {

        super(attacker);
        this.target = target;
        this.damage = damage;
        this.attackTypes.addAll(Arrays.asList(types));
        this.source = AttackSource.fromObject(attacker);
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

        if (damage < 0) {
            this.damage = 0;
        }
        this.damage = damage;
    }

    @Override
    public Set<AttackType> getAttackTypes() {

        return attackTypes;
    }

    @Override
    public void addAttackTypes(AttackType... types) {

        attackTypes.addAll(Arrays.asList(types));
    }

    @Override
    public boolean isOfAttackType(AttackType type) {

        return attackTypes.contains(type);
    }

    @Override
    public AttackSource getAttackSource() {

        return source;
    }

    @Override
    public boolean hasSource(AttackSource source) {

        return this.source == source;
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
