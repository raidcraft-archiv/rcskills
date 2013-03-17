package de.raidcraft.skills.api.combat.callback;

import de.raidcraft.skills.api.exceptions.CombatException;
import org.bukkit.Location;

/**
 * @author Silthus
 */
public interface LocationCallback extends ProjectileCallback<Location> {

    @Override
    void run(Location location) throws CombatException;
}
