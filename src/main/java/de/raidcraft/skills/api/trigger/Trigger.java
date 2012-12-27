/**
 *
 */
package de.raidcraft.skills.api.trigger;

import de.raidcraft.skills.api.hero.Hero;

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

    private final Hero hero;

    public Trigger(Hero hero) {

        this.hero = hero;
    }

    public Hero getHero() {

        return hero;
    }

    /**
     * Get the static handler list of this event subclass.
     *
     * @return HandlerList to call event with
     */
    protected abstract HandlerList getHandlers();

    /**
     * Get event type name.
     *
     * @return event name
     */
    protected abstract String getTriggerName();

    public String toString() {

        return getTriggerName() + " (" + this.getClass().getName() + ")";
    }
}
