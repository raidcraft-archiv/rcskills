package de.raidcraft.skills.api.combat.action;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.combat.callback.RangedCallback;

/**
 * This attack is issued by a skill or the damage event itself.
 *
 * @author Silthus
 */
public class MagicalAttack extends EntityAttack {


    public MagicalAttack(CharacterTemplate source, CharacterTemplate target, int damage) {

        super(source, target, damage, EffectType.MAGICAL);
    }

    public MagicalAttack(CharacterTemplate attacker, CharacterTemplate target, RangedCallback callback) {

        super(attacker, target, callback, EffectType.MAGICAL);
    }

    public MagicalAttack(CharacterTemplate attacker, CharacterTemplate target, int damage, RangedCallback callback) {

        super(attacker, target, damage, callback, EffectType.MAGICAL);
    }
}
