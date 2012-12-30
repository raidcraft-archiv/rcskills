package de.raidcraft.skills.effects.potion;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectElement;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.ExpirableEffect;
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
        name = "Speed",
        description = "Das Ziel wird schneller",
        types = {EffectType.BUFF},
        elements = {EffectElement.LIGHT}
)
public class Speed<S> extends ExpirableEffect<S> {

    private int amplifier;
    
    public Speed(S source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
    }

    @Override
    public void load(ConfigurationSection data) {

        this.amplifier = data.getInt("potion-amplifier", 1);
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {
        target.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, (int)getDuration(), amplifier));
    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {
        for(PotionEffect potionEffect : target.getEntity().getActivePotionEffects()) {
            if(potionEffect.getType() == PotionEffectType.INVISIBILITY) {
                target.getEntity().removePotionEffect(PotionEffectType.INVISIBILITY);
            }
        }
    }

    @Override
    protected void renew(CharacterTemplate target) throws CombatException {
        target.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, (int)getDuration(), amplifier));
    }
}
