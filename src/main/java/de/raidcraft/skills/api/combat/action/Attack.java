package de.raidcraft.skills.api.combat.action;

import de.raidcraft.api.items.CustomItemStack;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.AttackSource;
import de.raidcraft.skills.api.combat.EffectElement;
import de.raidcraft.skills.api.combat.EffectType;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Collection;
import java.util.Set;

/**
 * @author Silthus
 */
public interface Attack<S, T> extends TargetedAction<S, T> {

    double getDamage();

    void setDamage(double damage);

    boolean hasKnockback();

    void setKnockback(boolean knockback);

    default EntityDamageEvent.DamageCause getCause() {

        return EntityDamageEvent.DamageCause.CUSTOM;
    }

    Set<EffectElement> getAttackElements();

    void addAttackElement(Collection<EffectElement> elements);

    boolean isOfAttackElement(EffectElement element);

    Set<EffectType> getAttackTypes();

    void addAttackTypes(EffectType... type);

    boolean isOfAttackType(EffectType type);

    Set<CustomItemStack> getWeapons();

    void addWeapon(CustomItemStack weapon);

    void addWeapons(Collection<CustomItemStack> weapons);

    AttackSource getAttackSource();

    boolean hasSource(AttackSource source);

    CharacterTemplate getAttacker();

    boolean isAttacker(CharacterTemplate source);

}
