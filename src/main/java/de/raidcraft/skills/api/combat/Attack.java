package de.raidcraft.skills.api.combat;

import de.raidcraft.skills.api.hero.Hero;
import org.bukkit.entity.LivingEntity;

/**
 * @author Silthus
 */
public class Attack {

    public enum Result {

        SUCCESS,
        EVADED,
        REDUCED,
        MISSED,
        ABSORBED,
        REFLECTED,
        IMMUNE;
    }

    private Hero attacker;
    private LivingEntity victim;
    private double sourceDamage;
    private double targetDamage;
    private Result result;
}
