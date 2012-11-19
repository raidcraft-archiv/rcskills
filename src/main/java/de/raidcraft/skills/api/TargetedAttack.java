package de.raidcraft.skills.api;

import de.raidcraft.skills.api.exceptions.CombatException;
import org.bukkit.entity.LivingEntity;

/**
 * @author Silthus
 */
public interface TargetedAttack extends Active<LivingEntity> {

    public void run(LivingEntity target) throws CombatException;
}
