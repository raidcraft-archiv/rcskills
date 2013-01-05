package de.raidcraft.skills.api.combat.action;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.combat.callback.Callback;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * This attack is issued by a skill or the damage event itself.
 *
 * @author Silthus
 */
public class PhysicalAttack extends EntityAttack {


    public PhysicalAttack(CharacterTemplate source, CharacterTemplate target, int damage) {

        super(source, target, damage, EffectType.PHYSICAL);
    }

    public PhysicalAttack(CharacterTemplate attacker, CharacterTemplate target, Callback<CharacterTemplate> callback) {

        super(attacker, target, callback, EffectType.PHYSICAL);
    }

    public PhysicalAttack(CharacterTemplate attacker, CharacterTemplate target, int damage, Callback<CharacterTemplate> callback) {

        super(attacker, target, damage, callback, EffectType.PHYSICAL);
    }

    public PhysicalAttack(EntityDamageByEntityEvent event, int damage) {

        super(event, damage);
    }
}
