package de.raidcraft.skills.api.combat;

import org.bukkit.entity.LivingEntity;

/**
 * @author Silthus
 */
public interface Callback {

    public void run(LivingEntity entity);
}
