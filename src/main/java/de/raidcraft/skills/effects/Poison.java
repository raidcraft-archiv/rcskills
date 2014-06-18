package de.raidcraft.skills.effects;

import de.raidcraft.skills.api.ability.Ability;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectElement;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.combat.action.EffectDamage;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.types.PeriodicExpirableEffect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * @author Silthus
 */
@EffectInformation(
        name = "Poison",
        description = "Das Ziel ist vergiftet.",
        types = {EffectType.HARMFUL, EffectType.MAGICAL, EffectType.DAMAGING, EffectType.PURGEABLE, EffectType.DEBUFF},
        elements = {EffectElement.EARTH}
)
public class Poison extends PeriodicExpirableEffect<Ability> {

    private PotionEffect potionEffect;

    public Poison(Ability source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
        potionEffect = new PotionEffect(PotionEffectType.POISON, (int) getDuration(), 0, true);
    }

    @Override
    protected void tick(CharacterTemplate target) throws CombatException {

        new EffectDamage(this, getDamage()).run();
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {

        renew(target);
    }

    @Override
    protected void renew(CharacterTemplate target) throws CombatException {

        target.getEntity().addPotionEffect(potionEffect, true);
    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {

        target.getEntity().removePotionEffect(PotionEffectType.POISON);
    }
}
