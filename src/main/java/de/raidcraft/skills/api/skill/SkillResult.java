package de.raidcraft.skills.api.skill;

/**
 * @author Silthus
 */
public enum SkillResult {

    CANCELLED(false),
    INVALID_TARGET(true),
    FAIL(false),
    LOW_MANA(true),
    LOW_HEALTH(true),
    LOW_LEVEL(true),
    LOW_STAMINA(true),
    MISSING_REAGENT(true),
    NO_COMBAT(true),
    NORMAL(false),
    ON_GLOBAL_COOLDOWN(false),
    ON_COOLDOWN(true),
    REMOVED_EFFECT(false),
    SKIP_POST_USAGE(false),
    START_DELAY(false);

    private final boolean showMessage;

    private SkillResult(boolean showMessage) {

        this.showMessage = showMessage;
    }

    public boolean showMessage() {

        return showMessage;
    }
}
