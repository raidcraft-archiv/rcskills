package de.raidcraft.skills.api.combat.action;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.callback.Callback;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * @author Silthus
 */
public class MagicalAttack extends PhysicalAttack {

    public MagicalAttack(CharacterTemplate source, CharacterTemplate target, int damage) {

        super(source, target, damage);
    }

    public MagicalAttack(CharacterTemplate attacker, CharacterTemplate target, Callback callback) {

        super(attacker, target, callback);
    }

    public MagicalAttack(CharacterTemplate attacker, CharacterTemplate target, int damage, Callback callback) {

        super(attacker, target, damage, callback);
    }

    public MagicalAttack(EntityDamageByEntityEvent event) {

        super(event);
    }
}
