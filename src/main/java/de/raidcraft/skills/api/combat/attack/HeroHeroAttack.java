package de.raidcraft.skills.api.combat.attack;

import de.raidcraft.skills.api.hero.Hero;

/**
 * @author Silthus
 */
public class HeroHeroAttack extends AbstractAttack<Hero, Hero> {

    public HeroHeroAttack(Hero attacker, Hero target) {

        super(attacker, target, attacker.getDamage());
    }

    @Override
    public void run() {
        //TODO: implement
    }
}
