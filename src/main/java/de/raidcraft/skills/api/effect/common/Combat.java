package de.raidcraft.skills.api.effect.common;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.ExpirableEffect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;

/**
 * @author Silthus
 */
@EffectInformation(
        name = "Combat",
        description = "Is applied when a character enters combat",
        priority = 1.0
)
public class Combat<S> extends ExpirableEffect<S> {

    public Combat(S source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
        // the combat effect should always have a default priority of 1.0
        setPriority(1.0);
        // lets set a default combat delay of 15s
        if (getDuration() == 0) duration = 300;
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {

        // TODO: do more stuff like moving items into the inventory
        target.setInCombat(true);
        info("Du hast den Kampf betreten.");
    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {

        // TODO: do more stuff like moving items into the inventory
        target.setInCombat(false);
        info("Du hast den Kampf verlassen.");
    }

    @Override
    protected void renew(CharacterTemplate target) {

        // silently set in combat again
        target.setInCombat(true);
    }
}
