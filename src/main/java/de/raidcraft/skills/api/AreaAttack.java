package de.raidcraft.skills.api;

import de.raidcraft.skills.api.exceptions.CombatException;
import org.bukkit.Location;

/**
 * @author Silthus
 */
public interface AreaAttack extends Active<Location> {

    public void run(Location location) throws CombatException;
}
