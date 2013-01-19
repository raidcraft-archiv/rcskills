package de.raidcraft.skills.api.combat.action;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.effect.common.Combat;
import de.raidcraft.skills.api.exceptions.CombatException;

/**
 * @author Silthus
 */
public abstract class AbstractAction<T> implements Action<T> {

    private final T source;

    protected AbstractAction(T source) {

        this.source = source;
        // lets trigger the combat effect
        if (source instanceof CharacterTemplate) {
            try {
                ((CharacterTemplate) source).addEffect(source, Combat.class);
            } catch (CombatException e) {
                RaidCraft.LOGGER.warning(e.getMessage());
            }
        }
    }

    @Override
    public T getSource() {

        return source;
    }
}
