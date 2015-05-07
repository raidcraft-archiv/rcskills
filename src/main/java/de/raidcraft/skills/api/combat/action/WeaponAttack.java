package de.raidcraft.skills.api.combat.action;

import de.raidcraft.api.items.CustomItemStack;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.combat.callback.EntityAttackCallback;
import lombok.Getter;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Collection;

/**
 * @author Silthus
 */
public class WeaponAttack extends PhysicalAttack {

    @Getter
    private final Collection<CustomItemStack> weapons;

    public WeaponAttack(CharacterTemplate source, CharacterTemplate target, Collection<CustomItemStack> weapons, double damage, EffectType... types) {

        super(source, target, damage, types);
        this.weapons = weapons;
    }

    public WeaponAttack(CharacterTemplate attacker, CharacterTemplate target, Collection<CustomItemStack> weapons, EntityAttackCallback callback, EffectType... types) {

        super(attacker, target, callback, types);
        this.weapons = weapons;
    }

    public WeaponAttack(CharacterTemplate attacker, CharacterTemplate target, Collection<CustomItemStack> weapons, double damage, EntityAttackCallback callback, EffectType... types) {

        super(attacker, target, damage, callback, types);
        this.weapons = weapons;
    }

    public WeaponAttack(EntityDamageByEntityEvent event, Collection<CustomItemStack> weapons, double damage) {

        super(event, damage);
        this.weapons = weapons;
    }
}
