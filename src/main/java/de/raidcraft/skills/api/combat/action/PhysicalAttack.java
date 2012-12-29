package de.raidcraft.skills.api.combat.action;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.AttackType;
import de.raidcraft.skills.api.combat.callback.Callback;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * This attack is issued by a skill or the damage event itself.
 *
 * @author Silthus
 */
public class PhysicalAttack extends EntityAttack {


    public PhysicalAttack(CharacterTemplate source, CharacterTemplate target, int damage) {

        super(source, target, damage, AttackType.PHYSICAL);
    }

    public PhysicalAttack(CharacterTemplate attacker, CharacterTemplate target, Callback callback) {

        super(attacker, target, callback, AttackType.PHYSICAL);
    }

    public PhysicalAttack(CharacterTemplate attacker, CharacterTemplate target, int damage, Callback callback) {

        super(attacker, target, damage, callback, AttackType.PHYSICAL);
    }

    public PhysicalAttack(EntityDamageByEntityEvent event, int damage) {

        super(event, damage);
    }
}
