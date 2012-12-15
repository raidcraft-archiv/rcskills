package de.raidcraft.skills.api.combat.attack;

import de.raidcraft.skills.api.hero.Hero;
import org.bukkit.entity.LivingEntity;

/**
 * @author Silthus
 */
public class HeroEntityAttack extends AbstractAttack<Hero, LivingEntity> {

    protected HeroEntityAttack(Hero attacker, LivingEntity target) {

        super(attacker, target, attacker.getDamage());
    }

    @Override
    public void run() {
        //TODO: implement
    }
}
