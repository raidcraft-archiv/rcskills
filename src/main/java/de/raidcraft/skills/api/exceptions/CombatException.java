package de.raidcraft.skills.api.exceptions;

import de.raidcraft.api.RaidCraftException;

/**
 * @author Silthus
 */
public class CombatException extends RaidCraftException {

    public enum Type {

        CANCELLED("Abgebrochen."),
        INVALID_TARGET("Ungültiges Ziel."),
        INVALID_WEAPON("Du kannst nicht mit dieser Waffe angreifen."),
        FAIL("Fehlgeschlagen."),
        LOW_MANA("Zu wenig Mana."),
        LOW_HEALTH("Zu wenig Leben."),
        LOW_LEVEL("Dein Level ist zu niedrig."),
        LOW_STAMINA("Zu wenig Ausdauer."),
        MISSING_REAGENT("Fehlendes Reagenz."),
        NO_COMBAT("Kann nicht im Kampf verwendet werden."),
        COMBAT_ONLY("Kann nur im Kampf verwendet werden."),
        ON_GLOBAL_COOLDOWN("Globaler Cooldown."),
        ON_COOLDOWN("Abklingzeit nicht vorbei."),
        IMMUNE("Ziel ist immun."),
        EVADED("Deinem Angriff wurde ausgewichen."),
        PARRIED("Dein Angriff wurde parriert."),
        PASSIVE("Passive Skills können nicht gezaubert werden."),
        UNKNOWN("Unbekannter Fehlschlag."),
        SILENCED("Ein Stille Effekt ist aktiv! Du kannst keine Zauber wirken."),
        DISARMED("Du wurdest entwaffnet und kannst nicht angreifen!"),
        OUT_OF_RANGE("Ziel ist nicht in Reichweite."),
        SWING_COOLDOWN("Du kannst mit dieser Waffe gerade nicht angreifen."),
        NO_GROUP("Dein Ziel ist nicht in deiner Gruppe.");

        private final String message;

        private Type(String message) {

            this.message = message;
        }

        public String getMessage() {

            return message;
        }
    }

    private final Type type;

    public CombatException(String message) {

        super(message);
        this.type = Type.UNKNOWN;
    }

    public CombatException(Type type) {

        this(type, type.getMessage());
    }

    public CombatException(Type type, String message) {

        super(message);
        this.type = type;
    }

    public Type getType() {

        return type;
    }
}
