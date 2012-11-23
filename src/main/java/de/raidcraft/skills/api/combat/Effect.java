package de.raidcraft.skills.api.combat;

import org.bukkit.entity.LivingEntity;

/**
 * @author Silthus
 */
public interface Effect {

    public enum Result {

        APPLIED,
        IMMUNE;
        // TODO: add more effect results
    }

    public Result apply(LivingEntity target);
}
