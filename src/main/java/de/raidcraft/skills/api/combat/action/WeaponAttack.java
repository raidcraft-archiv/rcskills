package de.raidcraft.skills.api.combat.action;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.combat.callback.EntityAttackCallback;
import de.raidcraft.skills.items.Weapon;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * @author Silthus
 */
public class WeaponAttack extends PhysicalAttack {

    private final Weapon weapon;

    public WeaponAttack(CharacterTemplate source, CharacterTemplate target, Weapon weapon, int damage, EffectType... types) {

        super(source, target, damage, types);
        this.weapon = weapon;
    }

    public WeaponAttack(CharacterTemplate attacker, CharacterTemplate target, Weapon weapon, EntityAttackCallback callback, EffectType... types) {

        super(attacker, target, callback, types);
        this.weapon = weapon;
    }

    public WeaponAttack(CharacterTemplate attacker, CharacterTemplate target, Weapon weapon, int damage, EntityAttackCallback callback, EffectType... types) {

        super(attacker, target, damage, callback, types);
        this.weapon = weapon;
    }

    public WeaponAttack(EntityDamageByEntityEvent event, Weapon weapon, int damage) {

        super(event, damage);
        this.weapon = weapon;
    }

    public Weapon getWeapon() {

        return weapon;
    }
}
