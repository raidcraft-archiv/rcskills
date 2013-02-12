package de.raidcraft.skills.effects.disabling;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.effect.AbstractEffect;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.common.CastTime;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
@EffectInformation(
        name = "Interrupt",
        description = "Unterbricht den aktuellen Zauber des Gegners.",
        types = {EffectType.HARMFUL}
)
public class Interrupt<S> extends AbstractEffect<S> {

    private boolean silence = false;

    public Interrupt(S source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
    }

    @Override
    public void load(ConfigurationSection data) {

        silence = data.getBoolean("silence", false);
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {

        if (target.hasEffect(CastTime.class)) {
            // interrupt all spells that are currently casted
            target.removeEffect(CastTime.class);
            if (silence) target.addEffect(getSource(), Silence.class);
        }
        // and remove ourself directly after
        remove();
    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {

    }

    @Override
    protected void renew(CharacterTemplate target) throws CombatException {

    }
}
