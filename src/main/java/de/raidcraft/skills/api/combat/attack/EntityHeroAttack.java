package de.raidcraft.skills.api.combat.attack;

import de.raidcraft.skills.api.hero.Hero;
import org.bukkit.entity.LivingEntity;

/**
 * @author Silthus
 */
public class EntityHeroAttack extends AbstractAttack<LivingEntity, Hero> {

    protected EntityHeroAttack(LivingEntity attacker, Hero target, int damage) {

        super(attacker, target, damage);
    }

    @Override
    public void run() {
        //TODO: implement
    }
}
