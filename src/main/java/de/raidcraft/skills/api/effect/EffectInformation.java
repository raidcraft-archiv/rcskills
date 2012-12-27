package de.raidcraft.skills.api.effect;

import de.raidcraft.skills.api.EffectElement;
import de.raidcraft.skills.api.EffectType;

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

    public EffectType[] types() default {};

    public EffectElement[] elements() default {};

    public double priority() default 0.0;
}