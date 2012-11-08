package de.raidcraft.skills.api;

import de.raidcraft.skills.api.trigger.Trigger;

/**
 * @author Silthus
 */
public interface Passive<T extends Trigger> {

    public void apply(T trigger);
}
