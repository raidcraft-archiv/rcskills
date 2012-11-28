package de.raidcraft.skills.api.exceptions;

import de.raidcraft.api.RaidCraftException;

/**
 * @author Silthus
 */
public class CombatException extends RaidCraftException {

    public enum FailCause {

        CANCELLED("Abgebrochen"),
        INVALID_TARGET("Ungültiges Ziel"),
        FAIL("Fehlgeschlagen"),
        LOW_MANA("Zu wenig Mana"),
        LOW_HEALTH("Zu wenig Leben"),
        LOW_LEVEL("Level ist zu niedrig"),
        LOW_STAMINA("Zu wenig Stamina"),
        MISSING_REAGENT("Fehlendes Reagenz"),
        NO_COMBAT("Kann nicht im Kampf verwendet werden"),
        ON_GLOBAL_COOLDOWN("Globaler Cooldown"),
        ON_COOLDOWN("Abklingzeit nicht vorbei"),
        IMMUNE("Ziel ist immun");

        private final String message;

        private FailCause(String message) {

            this.message = message;
        }

        public String getMessage() {

            return message;
        }
    }

    private final FailCause failCause;

    public CombatException(String message, FailCause failCause) {

        super(message);
        this.failCause = failCause;
    }

    public FailCause getFailCause() {

        return failCause;
    }
}
