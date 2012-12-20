package de.raidcraft.skills.api.combat.effect;

import de.raidcraft.api.config.DataMap;
import de.raidcraft.skills.api.exceptions.CombatException;

/**
 * An effect is something that can be applied to entities around in the world.
 * An effect can also be scheduled to apply periodically or after a set time.
 *
 * @author Silthus
 */
public interface Effect<S, T> {

    public enum Type {


    }

    public String getName();

    public String getDescription();

    public Type[] getTypes();

    public boolean isOfType(Type type);

    public double getPriority();

    public void setPriority(double priority);

    public S getSource();

    public T getTarget();

    public void load(DataMap data);

    public void apply() throws CombatException;
}
