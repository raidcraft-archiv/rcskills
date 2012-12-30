package de.raidcraft.skills.api.trigger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

/**
 * A list of event handlers, stored per-event. Based on lahwran's fevents.
 */
public class HandlerList {

    /**
     * Handler array. This field being an array is the key to this system's speed.
     */
    private volatile RegisteredTrigger[] handlers = null;

    /**
     * Dynamic handler lists. These are changed using register() and
     * unregister() and are automatically baked to the handlers array any
     * time they have changed.
     */
    private final ArrayList<RegisteredTrigger> handlerslots;

    /**
     * List of all HandlerLists which have been created, for use in bakeAll()
     */
    private static final ArrayList<HandlerList> allLists = new ArrayList<>();

    /**
     * Bake all handler lists. Best used just after all normal event
     * registration is complete, ie just after all plugins are loaded if
     * you're using fevents in a plugin system.
     */
    public static void bakeAll() {

        synchronized (allLists) {
            for (HandlerList h : allLists) {
                h.bake();
            }
        }
    }

    /**
     * Unregister all listeners from all handler lists.
     */
    public static void unregisterAll() {

        synchronized (allLists) {
            for (HandlerList handlerList : allLists) {
                handlerList.handlerslots.clear();
                handlerList.handlers = null;
            }
        }
    }

    /**
     * Unregister a specific listener from all handler lists.
     *
     * @param listener listener to unregister
     */
    public static void unregisterAll(Triggered listener) {

        synchronized (allLists) {
            for (HandlerList handlerList : allLists) {
                handlerList.unregister(listener);
            }
        }
    }

    /**
     * Create a new handler list and initialize using EventPriority
     * The HandlerList is then added to meta-list for use in bakeAll()
     */
    public HandlerList() {

        handlerslots = new ArrayList<>();
        synchronized (allLists) {
            allLists.add(this);
        }
    }

    /**
     * Register a new listener in this handler list
     *
     * @param listener listener to register
     */
    public synchronized void register(RegisteredTrigger listener) {

        handlers = null;
        handlerslots.add(listener);
    }

    /**
     * Register a collection of new listeners in this handler list
     *
     * @param listeners listeners to register
     */
    public void registerAll(Collection<RegisteredTrigger> listeners) {

        for (RegisteredTrigger listener : listeners) {
            register(listener);
        }
    }

    /**
     * Remove a listener from a specific order slot
     *
     * @param listener listener to remove
     */
    public synchronized void unregister(RegisteredTrigger listener) {

        handlerslots.remove(listener);
        handlers = null;
    }

    /**
     * Remove a specific listener from this handler
     *
     * @param listener listener to remove
     */
    public synchronized void unregister(Triggered listener) {

        boolean changed = false;
        for (ListIterator<RegisteredTrigger> i = handlerslots.listIterator(); i.hasNext(); ) {
            if (i.next().getListener().equals(listener)) {
                i.remove();
                changed = true;
            }
        }
        if (changed) handlers = null;
    }

    /**
     * Bake HashMap and ArrayLists to 2d array - does nothing if not necessary
     */
    public synchronized void bake() {

        if (handlers != null) return; // don't re-bake when still valid
        List<RegisteredTrigger> entries = new ArrayList<>();
        entries.addAll(handlerslots);
        handlers = entries.toArray(new RegisteredTrigger[entries.size()]);
    }

    /**
     * Get the baked registered listeners associated with this handler list
     *
     * @return the array of registered listeners
     */
    public RegisteredTrigger[] getRegisteredListeners() {

        RegisteredTrigger[] handlers;
        while ((handlers = this.handlers) == null) bake(); // This prevents fringe cases of returning null
        return handlers;
    }

    /**
     * Get a list of all handler lists for every event type
     *
     * @return the list of all handler lists
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<HandlerList> getHandlerLists() {

        synchronized (allLists) {
            return (ArrayList<HandlerList>) allLists.clone();
        }
    }
}