package de.raidcraft.skills.api.combat.action;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.AttackType;
import de.raidcraft.skills.api.combat.callback.Callback;

/**
 * This attack is issued by a skill or the damage event itself.
 *
 * @author Silthus
 */
public class MagicalAttack extends EntityAttack {


    public MagicalAttack(CharacterTemplate source, CharacterTemplate target, int damage) {

        super(source, target, damage, AttackType.MAGICAL);
    }

    public MagicalAttack(CharacterTemplate attacker, CharacterTemplate target, Callback callback) {

        super(attacker, target, callback, AttackType.MAGICAL);
    }

    public MagicalAttack(CharacterTemplate attacker, CharacterTemplate target, int damage, Callback callback) {

        super(attacker, target, damage, callback, AttackType.MAGICAL);
    }
}
