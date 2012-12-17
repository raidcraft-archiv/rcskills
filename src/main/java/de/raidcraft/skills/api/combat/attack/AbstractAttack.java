package de.raidcraft.skills.api.combat.attack;

import de.raidcraft.skills.api.character.CharacterTemplate;

/**
 * @author Silthus
 */
public abstract class AbstractAttack implements Attack {

    private final CharacterTemplate attacker;
    private final CharacterTemplate target;
    private int damage;

    protected AbstractAttack(CharacterTemplate attacker, CharacterTemplate target, int damage) {

        this.attacker = attacker;
        this.target = target;
        this.damage = damage;
    }

    @Override
    public CharacterTemplate getAttacker() {

        return attacker;
    }

    @Override
    public CharacterTemplate getTarget() {

        return target;
    }

    @Override
    public int getDamage() {

        return damage;
    }

    @Override
    public void setDamage(int damage) {

        this.damage = damage;
    }
}
