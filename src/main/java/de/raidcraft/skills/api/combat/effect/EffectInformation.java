package de.raidcraft.skills.api.combat.effect;

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

    public String name();

    public String description();

    public Effect.Type[] types();

    public double priority() default 0.0;
}