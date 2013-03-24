package de.raidcraft.skills.api.combat.action;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.AttackSource;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.effect.common.Combat;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;

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
    private final Set<EffectType> attackTypes = new HashSet<>();
    private final AttackSource source;

    public AbstractAttack(S attacker, T target, int damage, EffectType... types) {

        super(attacker);
        this.target = target;
        this.damage = damage;
        this.attackTypes.addAll(Arrays.asList(types));
        this.source = AttackSource.fromObject(attacker);
        // lets trigger the combat effect
        if (attacker instanceof CharacterTemplate) {
            try {
                ((CharacterTemplate)attacker).addEffect(source, Combat.class);
            } catch (CombatException e) {
                RaidCraft.LOGGER.warning(e.getMessage());
            }
        }
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
    public Set<EffectType> getAttackTypes() {

        return attackTypes;
    }

    @Override
    public void addAttackTypes(EffectType... types) {

        attackTypes.addAll(Arrays.asList(types));
    }

    @Override
    public boolean isOfAttackType(EffectType type) {

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
