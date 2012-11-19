package de.raidcraft.skills.api.hero;

import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.skills.api.Levelable;

/**
 * @author Silthus
 */
public interface Hero extends Levelable {

    public RCPlayer getRCPlayer();
}
