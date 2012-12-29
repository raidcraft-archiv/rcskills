package de.raidcraft.skills.api.combat.action;

import de.raidcraft.skills.api.combat.AttackSource;
import de.raidcraft.skills.api.combat.AttackType;
import org.bukkit.event.Cancellable;

import java.util.Set;

/**
 * @author Silthus
 */
public interface Attack<S, T> extends Action<S>, Cancellable {

    public T getTarget();

    public int getDamage();

    public void setDamage(int damage);

    public Set<AttackType> getAttackTypes();

    public void addAttackTypes(AttackType... type);

    public boolean isOfAttackType(AttackType type);

    public AttackSource getAttackSource();

    public boolean hasSource(AttackSource source);
}
