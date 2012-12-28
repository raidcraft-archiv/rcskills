package de.raidcraft.skills.effects.damaging;

import de.raidcraft.skills.api.combat.EffectElement;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.PeriodicExpirableEffect;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.api.trigger.TriggerHandler;
import de.raidcraft.skills.api.trigger.Triggered;
import de.raidcraft.skills.trigger.BlockBreakTrigger;
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
public class Burn<S> extends PeriodicExpirableEffect<S> implements Triggered {

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

    @TriggerHandler
    public void onBreak(BlockBreakTrigger trigger) {

        trigger.getHero().sendMessage("YAY!");
    }
}
