/**
 *
 */
package de.raidcraft.skills.api.trigger;

import de.raidcraft.skills.api.character.CharacterTemplate;

/**
 * Trigger superclass. should be extended as:
 * <pre>
 *     class MyTrigger extends Trigger<MyTrigger> {
 *         public static final HandlerList<MyTrigger> handlers = new HandlerList<MyTrigger>();
 *
 *         @Override
 *         HandlerList<MyTrigger> getHandlers() {
 *             return handlers;
 *         }
 *         @Override
 *         void call(Listener<MyTrigger> listener) {
 *             listener.onEvent(this);
 *         }
 *     }
 * </pre>
 *
 * @author lahwran
 */
public abstract class Trigger {

    private final CharacterTemplate source;
    private String name;

    public Trigger(CharacterTemplate source) {

        this.source = source;
    }

    public CharacterTemplate getSource() {

        return source;
    }

    /**
     * @return Name of this trigger
     */
    public String getTriggerName() {

        if (name == null) {
            name = getClass().getSimpleName();
        }
        return name;
    }

    /**
     * Get the static handler list of this event subclass.
     *
     * @return HandlerList to call event with
     */
    protected abstract HandlerList getHandlers();
}
