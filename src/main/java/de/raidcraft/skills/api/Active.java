package de.raidcraft.skills.api;

import de.raidcraft.skills.api.trigger.Trigger;

/**
 * @author Silthus
 */
public interface Active<T extends Trigger> {

    public void run(T trigger);
}
