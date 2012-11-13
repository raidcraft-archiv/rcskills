package de.raidcraft.skills.api.trigger;

import de.raidcraft.api.player.RCPlayer;

/**
 * @author Silthus
 */
public interface Trigger {

    public <T extends Trigger> T getTrigger(Class<T> clazz);

    public RCPlayer getPlayer();
}
