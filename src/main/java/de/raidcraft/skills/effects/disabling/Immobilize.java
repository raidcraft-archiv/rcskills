package de.raidcraft.skills.effects.disabling;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.types.ExpirableEffect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * @author Silthus
 */
@EffectInformation(
        name = "Immobilize",
        description = "Immobilizes the target completlly locking it in place.",
        types = {EffectType.HARMFUL, EffectType.MOVEMENT}
)
public class Immobilize<S> extends ExpirableEffect<S> {

    private final PotionEffect jumpBlock;
    private final PotionEffect moveBlock;

    public Immobilize(S source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
        jumpBlock = new PotionEffect(PotionEffectType.JUMP, (int) getDuration(), 128, false);
        moveBlock = new PotionEffect(PotionEffectType.SLOW, (int) getDuration(), 6, false);
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {

        renew(target);
    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {

        target.getEntity().removePotionEffect(PotionEffectType.JUMP);
        target.getEntity().removePotionEffect(PotionEffectType.SLOW);
    }

    @Override
    protected void renew(CharacterTemplate target) throws CombatException {

        target.getEntity().addPotionEffect(jumpBlock);
        target.getEntity().addPotionEffect(moveBlock);
    }
}
