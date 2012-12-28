package de.raidcraft.skills.api.trigger;

import de.raidcraft.RaidCraft;
import org.bukkit.event.EventException;
import org.bukkit.plugin.IllegalPluginAccessException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * This class doesn't actually need to exist, but it feels wrong to have this
 * part of the event call logic inside Trigger
 *
 * @author lahwran
 */
public class TriggerManager {

    /**
     * Call an event.
     *
     * @param trigger    Trigger to handle
     */
    public static void callTrigger(Trigger trigger) {

        HandlerList handlerlist = trigger.getHandlers();
        handlerlist.bake();

        RegisteredTrigger[] handlers = handlerlist.getRegisteredListeners();

        for (RegisteredTrigger listener : handlers) {
            try {
                listener.callTrigger(trigger);
            } catch (Throwable t) {
                System.err.println("Error while passing trigger " + trigger);
                t.printStackTrace();
            }
        }
    }

    public static void registerListeners(Triggered listener) {

        for (Map.Entry<Class<? extends Trigger>, Set<RegisteredTrigger>> entry : createRegisteredTriggers(listener).entrySet()) {
            getTriggerListeners(getRegistrationClass(entry.getKey())).registerAll(entry.getValue());
        }
    }

    private static HandlerList getTriggerListeners(Class<? extends Trigger> type) {
        try {
            Method method = getRegistrationClass(type).getDeclaredMethod("getHandlerList");
            method.setAccessible(true);
            return (HandlerList) method.invoke(null);
        } catch (Exception e) {
            throw new IllegalPluginAccessException(e.toString());
        }
    }

    private static Class<? extends Trigger> getRegistrationClass(Class<? extends Trigger> clazz) {
        try {
            clazz.getDeclaredMethod("getHandlerList");
            return clazz;
        } catch (NoSuchMethodException e) {
            if (clazz.getSuperclass() != null
                    && !clazz.getSuperclass().equals(Trigger.class)
                    && Trigger.class.isAssignableFrom(clazz.getSuperclass())) {
                return getRegistrationClass(clazz.getSuperclass().asSubclass(Trigger.class));
            } else {
                throw new IllegalPluginAccessException("Unable to find handler list for event " + clazz.getName());
            }
        }
    }

    public static Map<Class<? extends Trigger>, Set<RegisteredTrigger>> createRegisteredTriggers(Triggered listener) {

        Map<Class<? extends Trigger>, Set<RegisteredTrigger>> ret = new HashMap<>();
        Set<Method> methods;

            Method[] publicMethods = listener.getClass().getMethods();
            methods = new HashSet<>(publicMethods.length, Float.MAX_VALUE);
            Collections.addAll(methods, publicMethods);
            Collections.addAll(methods, listener.getClass().getDeclaredMethods());

        for (final Method method : methods) {

            if (!method.isAnnotationPresent(TriggerHandler.class)) continue;

            TriggerHandler annotation = method.getAnnotation(TriggerHandler.class);
            final Class<?> checkClass = method.getParameterTypes()[0];
            if (!Trigger.class.isAssignableFrom(checkClass) || method.getParameterTypes().length != 1) {
                RaidCraft.LOGGER.severe("SkillsPlugin attempted to register an invalid TriggerHandler method signature \"" + method.toGenericString() + "\" in " + listener.getClass());
                continue;
            }
            final Class<? extends Trigger> eventClass = checkClass.asSubclass(Trigger.class);
            method.setAccessible(true);
            Set<RegisteredTrigger> eventSet = ret.get(eventClass);
            if (eventSet == null) {
                eventSet = new HashSet<>();
                ret.put(eventClass, eventSet);
            }

            TriggerExecutor executor = new TriggerExecutor() {
                public void execute(Triggered listener, Trigger event) throws EventException {
                    try {
                        if (!eventClass.isAssignableFrom(event.getClass())) {
                            return;
                        }
                        method.invoke(listener, event);
                    } catch (InvocationTargetException ex) {
                        throw new EventException(ex.getCause());
                    } catch (Throwable t) {
                        throw new EventException(t);
                    }
                }
            };
            eventSet.add(new RegisteredTrigger(listener, executor, annotation.ignoreChecks(), annotation.cancelEventOnFail()));
        }
        return ret;
    }
}
