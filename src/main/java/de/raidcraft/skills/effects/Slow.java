package de.raidcraft.skills.effects;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.types.ExpirableEffect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Author: Philip
 * Date: 27.12.12 - 21:45
 * Description:
 */
@EffectInformation(
        name = "Slow",
        description = "Das Ziel wird verlangsamt",
        types = {EffectType.DEBUFF, EffectType.MOVEMENT}
)
public class Slow<S> extends ExpirableEffect<S> {

    private int amplifier;

    public Slow(S source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
    }

    @Override
    public void load(ConfigurationSection data) {

        this.amplifier = data.getInt("potion-amplifier", 1);
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {

        target.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (int) getDuration(), amplifier));
    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {

        for (PotionEffect potionEffect : target.getEntity().getActivePotionEffects()) {
            if (potionEffect.getType() == PotionEffectType.SLOW) {
                target.getEntity().removePotionEffect(PotionEffectType.SLOW);
            }
        }
    }

    @Override
    protected void renew(CharacterTemplate target) throws CombatException {

        target.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (int) getDuration(), amplifier));
    }
}
