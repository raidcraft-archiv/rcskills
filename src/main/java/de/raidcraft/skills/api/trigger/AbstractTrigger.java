package de.raidcraft.skills.api.trigger;

import de.raidcraft.api.player.RCPlayer;

/**
 * @author Silthus
 */
public abstract class AbstractTrigger implements Trigger {

    private final RCPlayer player;

    protected AbstractTrigger(RCPlayer player) {

        this.player = player;
    }

    @Override
    public <T extends Trigger> T getTrigger(Class<T> clazz) {

        return clazz.cast(this);
    }

    @Override
    public RCPlayer getPlayer() {

        return player;
    }
}
