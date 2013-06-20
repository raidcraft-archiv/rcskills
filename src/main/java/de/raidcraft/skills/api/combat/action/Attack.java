package de.raidcraft.skills.api.combat.action;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.AttackSource;
import de.raidcraft.skills.api.combat.EffectElement;
import de.raidcraft.skills.api.combat.EffectType;

import java.util.Collection;
import java.util.Set;

/**
 * @author Silthus
 */
public interface Attack<S, T> extends TargetedAction<S, T> {

    public int getDamage();

    public void setDamage(int damage);

    public boolean hasKnockback();

    public void setKnockback(boolean knockback);

    public Set<EffectElement> getAttackElements();

    public void addAttackElement(Collection<EffectElement> elements);

    public boolean isOfAttackElement(EffectElement element);

    public Set<EffectType> getAttackTypes();

    public void addAttackTypes(EffectType... type);

    public boolean isOfAttackType(EffectType type);

    public AttackSource getAttackSource();

    public boolean hasSource(AttackSource source);

    public CharacterTemplate getAttacker();

    public boolean isSource(CharacterTemplate source);

}
