package de.raidcraft.skills.api.combat.attack;

import de.raidcraft.skills.api.hero.Hero;
import org.bukkit.entity.LivingEntity;

/**
 * @author Silthus
 */
public class EntityHeroAttack extends AbstractAttack<LivingEntity, Hero> {

    public EntityHeroAttack(LivingEntity attacker, Hero target, int damage) {

        super(attacker, target, damage);
    }

    @Override
    public void run() {

        // this means the damage is passed from the EntityDamageByEntity Event
        // the damage in the event was set to 0 so we can handle the damage for the hero here
        getTarget().damage(getDamage());
    }
}
