package de.raidcraft.skills.effects;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectElement;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.types.ExpirableEffect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Author: Philip
 * Date: 27.12.12 - 21:45
 * Description:
 */
@EffectInformation(
        name = "Blind",
        description = "Lässt das Ziel erblinden",
        types = {EffectType.DEBUFF},
        elements = {EffectElement.DARK}
)
public class Blind<S> extends ExpirableEffect<S> {

    public Blind(S source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {

        target.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (int) getDuration(), 1));
    }

    @Override
    protected void renew(CharacterTemplate target) throws CombatException {

        target.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (int) getDuration(), 1));
    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {

        for (PotionEffect potionEffect : target.getEntity().getActivePotionEffects()) {
            if (potionEffect.getType() == PotionEffectType.BLINDNESS) {
                target.getEntity().removePotionEffect(PotionEffectType.BLINDNESS);
            }
        }
    }
}
