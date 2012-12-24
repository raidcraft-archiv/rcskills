package de.raidcraft.skills.effects.damaging;

import de.raidcraft.skills.api.EffectElement;
import de.raidcraft.skills.api.EffectType;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.PeriodicExpirableEffect;
import de.raidcraft.skills.api.persistance.EffectData;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
@EffectInformation(
        name = "Burn",
        description = "Verbrennt das Ziel",
        types = {EffectType.HARMFUL, EffectType.DAMAGING, EffectType.DEBUFF},
        elements = {EffectElement.FIRE}
)
public class Burn<S> extends PeriodicExpirableEffect<S> {

    private int fireTicks;

    public Burn(S source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
    }

    @Override
    public void load(ConfigurationSection data) {

        this.fireTicks = data.getInt("fire-ticks", 60);
    }

    @Override
    public void apply(CharacterTemplate target) {

        target.getEntity().setFireTicks(fireTicks);
    }

    @Override
    protected void remove(CharacterTemplate target) {

        target.getEntity().setFireTicks(0);
    }

    @Override
    protected void renew(CharacterTemplate target) {

        target.getEntity().setFireTicks(fireTicks);
    }

    @Override
    protected void tick(CharacterTemplate target) {

        target.getEntity().setFireTicks(fireTicks);
    }
}
