package de.raidcraft.skills.api.combat.action;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.api.ability.Ability;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.AttackSource;
import de.raidcraft.skills.api.combat.EffectElement;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.effect.Effect;
import de.raidcraft.skills.api.effect.common.Combat;
import de.raidcraft.skills.api.exceptions.CombatException;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Silthus
 */
public abstract class AbstractAttack<S, T> extends AbstractTargetedAction<S, T> implements Attack<S, T> {

    private int damage;
    private boolean knockback;
    private final Set<EffectType> attackTypes = new HashSet<>();
    private final Set<EffectElement> attackElemens = new HashSet<>();
    private final AttackSource source;

    public AbstractAttack(S attacker, T target, int damage, EffectType... types) {

        super(attacker, target);
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
    public int getThreat() {

        return getDamage();
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
    public boolean hasKnockback() {

        return knockback;
    }

    @Override
    public void setKnockback(boolean knockback) {

        this.knockback = knockback;
    }

    @Override
    public Set<EffectElement> getAttackElements() {

        return attackElemens;
    }

    @Override
    public void addAttackElement(Collection<EffectElement> elements) {

        attackElemens.addAll(elements);
    }

    @Override
    public boolean isOfAttackElement(EffectElement element) {

        return attackElemens.contains(element);
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
    public CharacterTemplate getAttacker() {

        if (getSource() instanceof CharacterTemplate) {
            return (CharacterTemplate) getSource();
        }
        if (getSource() instanceof Ability) {
            return ((Ability) getSource()).getHolder();
        }
        if (getSource() instanceof Effect
                && ((Effect) getSource()).getSource() instanceof Ability) {
            return ((Ability) ((Effect) getSource()).getSource()).getHolder();
        }
        return null;
    }

    @Override
    public boolean isSource(CharacterTemplate source) {

        CharacterTemplate attacker = getAttacker();
        return attacker != null && attacker.equals(source);
    }
}
