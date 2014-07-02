package de.raidcraft.skills.api.trigger;

/**
 * @author Silthus
 */
public enum TriggerPriority {

    /**
     * Trigger call is of very low importance and should be ran first, to allow
     * other listeners to further customise the outcome
     */
    LOWEST(0),
    /**
     * Trigger call is of low importance
     */
    LOW(1),
    /**
     * Trigger call is neither important or unimportant, and may be ran normally
     */
    NORMAL(2),
    /**
     * Trigger call is of high importance
     */
    HIGH(3),
    /**
     * Trigger call is critical and must have the final say in what happens
     * to the trigger
     */
    HIGHEST(4),
    /**
     * Trigger is listened to purely for monitoring the outcome of an trigger.
     *
     * No modifications to the trigger should be made under this priority
     */
    MONITOR(5);

    private final int slot;

    private TriggerPriority(int slot) {

        this.slot = slot;
    }

    public int getSlot() {

        return slot;
    }
}
