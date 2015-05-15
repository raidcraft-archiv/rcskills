package de.raidcraft.skills.api.effect;

import de.raidcraft.skills.api.combat.EffectElement;
import de.raidcraft.skills.api.combat.EffectType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Silthus
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EffectInformation {

    String name();

    String description();

    String[] configUsage() default {};

    EffectType[] types() default {};

    EffectElement[] elements() default {};

    DiminishingReturnType diminishingReturn() default DiminishingReturnType.NULL;

    double priority() default 0.0;

    boolean global() default false;
}