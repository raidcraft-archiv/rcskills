package de.raidcraft.skills.api.trigger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Silthus
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TriggerHandler {

    TriggerPriority priority() default TriggerPriority.NORMAL;

    boolean ignoreCancelled() default false;

    boolean filterTargets() default true;
}
