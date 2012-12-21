package de.raidcraft.skills.effects.common;

import de.raidcraft.api.config.DataMap;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.effect.AbstractPeriodicEffect;
import de.raidcraft.skills.api.combat.effect.EffectInformation;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;

/**
 * @author Silthus
 */
@EffectInformation(
        name = "BurnEffect",
        description = "Verbrennt das Ziel",
        types = {}
)
public class BurnEffect extends AbstractPeriodicEffect<CharacterTemplate> {

    private int fireTicks;

    public BurnEffect(CharacterTemplate source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
    }

    @Override
    public void load(DataMap data) {

        this.fireTicks = data.getInt("fire-ticks", 60);
    }

    @Override
    public void apply(CharacterTemplate target) throws CombatException {

        target.getEntity().setFireTicks(fireTicks);
    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {

        target.getEntity().setFireTicks(0);
    }

    @Override
    protected void renew(CharacterTemplate target) {

        target.getEntity().setFireTicks(fireTicks);
    }
}
