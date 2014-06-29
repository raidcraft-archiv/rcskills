package de.raidcraft.skills.api.effect.common;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.types.ExpirableEffect;
import de.raidcraft.skills.api.events.RCCombatEvent;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.EffectData;
import org.bukkit.entity.Creature;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Silthus
 */
@EffectInformation(
        name = "Combat",
        description = "Is applied when a character enters combat",
        types = {EffectType.SYSTEM},
        priority = 1.0,
        global = true
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
        RCCombatEvent event = new RCCombatEvent(getTarget(), RCCombatEvent.Type.ENTER);
        RaidCraft.callEvent(event);
        if (event.isCancelled()) {
            throw new CombatException(CombatException.Type.CANCELLED);
        }
        info("Du hast den Kampf betreten.");
        if (getSource() instanceof CharacterTemplate) {
            // als add the source to our list
            addInvolvedCharacter((CharacterTemplate) getSource());
        }
        addInvolvedCharacter(target);
    }

    @Override
    protected void renew(CharacterTemplate target) {

        // silently set in combat again
        target.setInCombat(true);
    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {

        target.setInCombat(false);
        if (target instanceof Hero) {
            RCCombatEvent event = new RCCombatEvent(getTarget(), RCCombatEvent.Type.LEAVE);
            RaidCraft.callEvent(event);
            if (event.isCancelled()) {
                throw new CombatException(CombatException.Type.CANCELLED);
            }
        } else if (target.getEntity() instanceof Creature) {
            // TODO: unset aggro range
        }
        info("Du hast den Kampf verlassen.");
    }

    public void addInvolvedCharacter(CharacterTemplate character) {

        involvedCharacters.add(character);
    }

    public boolean isInvolved(CharacterTemplate character) {

        return involvedCharacters.contains(character);
    }

    public Set<CharacterTemplate> getInvolvedCharacters() {

        return involvedCharacters;
    }
}
