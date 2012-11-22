package de.raidcraft.skills.api.combat;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.Skill;
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

    private final Hero attacker;
    private final LivingEntity victim;
    private final Skill skill;
    private double sourceDamage;
    private double targetDamage;
    private Result result;

    public Attack(Hero attacker, LivingEntity victim, Skill skill) {

        this.attacker = attacker;
        this.victim = victim;
        this.skill = skill;
    }
}
