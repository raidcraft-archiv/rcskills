package de.raidcraft.skills.api.exceptions;

import de.raidcraft.api.RaidCraftException;

/**
 * @author Silthus
 */
public class CombatException extends RaidCraftException {

    public enum Cause {

        CANCELLED,
        INVALID_TARGET,
        FAIL,
        LOW_MANA,
        LOW_HEALTH,
        LOW_LEVEL,
        LOW_STAMINA,
        MISSING_REAGENT,
        NO_COMBAT,
        ON_GLOBAL_COOLDOWN,
        ON_COOLDOWN,
        IMMUNE;
    }

    private final Cause[] causes;

    public CombatException(String message, Cause... causes) {

        super(message);
        this.causes = causes;
    }

    public Cause[] getCauses() {

        return causes;
    }
}
