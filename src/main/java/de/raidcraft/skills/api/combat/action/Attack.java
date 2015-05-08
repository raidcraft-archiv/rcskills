package de.raidcraft.skills.api.combat.action;

import de.raidcraft.api.items.CustomItemStack;
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

    public double getDamage();

    public void setDamage(double damage);

    public boolean hasKnockback();

    public void setKnockback(boolean knockback);

    public Set<EffectElement> getAttackElements();

    public void addAttackElement(Collection<EffectElement> elements);

    public boolean isOfAttackElement(EffectElement element);

    public Set<EffectType> getAttackTypes();

    public void addAttackTypes(EffectType... type);

    public boolean isOfAttackType(EffectType type);

    public Set<CustomItemStack> getWeapons();

    public void addWeapon(CustomItemStack weapon);

    public void addWeapons(Collection<CustomItemStack> weapons);

    public AttackSource getAttackSource();

    public boolean hasSource(AttackSource source);

    public CharacterTemplate getAttacker();

    public boolean isAttacker(CharacterTemplate source);

}
