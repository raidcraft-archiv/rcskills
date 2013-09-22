package de.raidcraft.skills.api.combat.action;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.combat.callback.EntityAttackCallback;

/**
 * This attack is issued by a skill or the damage event itself.
 *
 * @author Silthus
 */
public class MagicalAttack extends EntityAttack {


    public MagicalAttack(CharacterTemplate source, CharacterTemplate target, double damage) {

        super(source, target, damage, EffectType.MAGICAL);
    }

    public MagicalAttack(CharacterTemplate attacker, CharacterTemplate target, EntityAttackCallback callback) {

        super(attacker, target, callback, EffectType.MAGICAL);
    }

    public MagicalAttack(CharacterTemplate attacker, CharacterTemplate target, double damage, EntityAttackCallback callback) {

        super(attacker, target, damage, callback, EffectType.MAGICAL);
    }
}
