package de.raidcraft.skills.api.effect.common;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.types.ExpirableEffect;
import de.raidcraft.skills.api.events.RCCombatEvent;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.EffectData;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Silthus
 */
@EffectInformation(
        name = "Combat",
        description = "Is applied when a character enters combat",
        priority = 1.0
)
public class Combat<S> extends ExpirableEffect<S> {

    private final Set<CharacterTemplate> involvedCharacters = new HashSet<>();

    public Combat(S source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
        // the combat effect should always have a default priority of 1.0
        setPriority(1.0);
        // lets set a default combat delay of 15s
        if (getDuration() == 0) duration = 300;
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {

        target.setInCombat(true);
        if (target instanceof Hero) {
            RaidCraft.callEvent(new RCCombatEvent((Hero) getTarget(), RCCombatEvent.Type.ENTER));
        }
        info("Du hast den Kampf betreten.");
        if (getSource() instanceof CharacterTemplate) {
            // als add the source to our list
            addInvolvedCharacter((CharacterTemplate) getSource());
        }
        addInvolvedCharacter(target);
    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {

        target.setInCombat(false);
        if (target instanceof Hero) {
            RaidCraft.callEvent(new RCCombatEvent((Hero) getTarget(), RCCombatEvent.Type.LEAVE));
        }
        info("Du hast den Kampf verlassen.");
    }

    @Override
    protected void renew(CharacterTemplate target) {

        // silently set in combat again
        target.setInCombat(true);
    }

    public boolean isInvolved(CharacterTemplate character) {

        return involvedCharacters.contains(character);
    }

    public void addInvolvedCharacter(CharacterTemplate character) {

        involvedCharacters.add(character);
    }
}
