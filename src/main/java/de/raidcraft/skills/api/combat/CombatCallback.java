package de.raidcraft.skills.api.combat;

import org.bukkit.entity.LivingEntity;

/**
 * @author Silthus
 */
public interface CombatCallback {

    public void run(LivingEntity entity);
}
