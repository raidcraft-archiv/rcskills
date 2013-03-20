package de.raidcraft.skills.api.combat.action;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.combat.callback.EntityAttackCallback;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * This attack is issued by a skill or the damage event itself.
 *
 * @author Silthus
 */
public class PhysicalAttack extends EntityAttack {


    public PhysicalAttack(CharacterTemplate source, CharacterTemplate target, int damage, EffectType... types) {

        super(source, target, damage, EffectType.PHYSICAL);
        addAttackTypes(types);
    }

    public PhysicalAttack(CharacterTemplate attacker, CharacterTemplate target, EntityAttackCallback callback, EffectType... types) {

        super(attacker, target, callback, EffectType.PHYSICAL);
        addAttackTypes(types);
    }

    public PhysicalAttack(CharacterTemplate attacker, CharacterTemplate target, int damage, EntityAttackCallback callback, EffectType... types) {

        super(attacker, target, damage, callback, EffectType.PHYSICAL);
        addAttackTypes(types);
    }

    public PhysicalAttack(EntityDamageByEntityEvent event, int damage) {

        super(event, damage);
    }
}
