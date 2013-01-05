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

    public Trigger(CharacterTemplate source) {

        this.source = source;
    }

    public CharacterTemplate getSource() {

        return source;
    }

    /**
     * Get the static handler list of this event subclass.
     *
     * @return HandlerList to call event with
     */
    protected abstract HandlerList getHandlers();
}
