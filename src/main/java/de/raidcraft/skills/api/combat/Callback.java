package de.raidcraft.skills.api.combat;

import de.raidcraft.skills.api.exceptions.CombatException;
import org.bukkit.entity.LivingEntity;

/**
 * @author Silthus
 */
public interface Callback {

    public void run(LivingEntity entity) throws CombatException;
}
