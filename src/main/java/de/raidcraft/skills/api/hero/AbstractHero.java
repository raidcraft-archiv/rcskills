package de.raidcraft.skills.api.hero;

import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.skills.api.AbstractLevelable;
import de.raidcraft.skills.api.persistance.LevelData;

/**
 * @author Silthus
 */
public abstract class AbstractHero extends AbstractLevelable implements Hero {

    private final RCPlayer player;

    protected AbstractHero(RCPlayer player, LevelData data) {

        super(data);
        this.player = player;
    }

    @Override
    public Hero getHero() {
        return this;
    }

    @Override
    public RCPlayer getRCPlayer() {

        return player;
    }
}
