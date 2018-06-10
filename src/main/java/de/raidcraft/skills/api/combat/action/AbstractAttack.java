package de.raidcraft.skills.api.combat.action;

import de.raidcraft.api.ambient.AmbientEffect;
import de.raidcraft.api.items.CustomItemStack;
import de.raidcraft.skills.api.ability.Ability;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.AttackSource;
import de.raidcraft.skills.api.combat.EffectElement;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.effect.Effect;
import de.raidcraft.util.MathUtil;

import java.util.*;

/**
 * @author Silthus
 */
public abstract class AbstractAttack<S, T> extends AbstractTargetedAction<S, T> implements Attack<S, T> {

    private final Set<EffectType> attackTypes = new HashSet<>();
    private final Set<EffectElement> attackElemens = new HashSet<>();
    private final Set<CustomItemStack> weapons = new HashSet<>();
    private final AttackSource source;
    private double damage;
    private boolean knockback;
    private List<AmbientEffect> impactEffects = new ArrayList<>();

    public AbstractAttack(S attacker, T target, double damage, EffectType... types) {

        super(attacker, target);
        setDamage(damage);
        this.attackTypes.addAll(Arrays.asList(types));
        this.source = AttackSource.fromObject(attacker);
    }

    @Override
    public double getThreat() {

        return getDamage();
    }

    @Override
    public double getDamage() {

        return damage;
    }

    @Override
    public void setDamage(double damage) {

        if (damage < 0) {
            this.damage = 0;
        }
        this.damage = MathUtil.trim(damage);
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
    public Set<CustomItemStack> getWeapons() {

        return weapons;
    }

    @Override
    public void addWeapon(CustomItemStack weapon) {

        this.weapons.add(weapon);
    }

    @Override
    public void addWeapons(Collection<CustomItemStack> weapons) {

        this.weapons.addAll(weapons);
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
    public boolean isAttacker(CharacterTemplate source) {

        CharacterTemplate attacker = getAttacker();
        return attacker != null && attacker.equals(source);
    }

    protected List<AmbientEffect> getImpactEffects() {

        return this.impactEffects;
    }

    public void setImpactEffects(List<AmbientEffect> impactEffects) {

        this.impactEffects = impactEffects;
    }
}