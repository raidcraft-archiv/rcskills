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

    public String name();

    public String description();

    public String[] configUsage() default {};

    public EffectType[] types() default {};

    public EffectElement[] elements() default {};

    public DiminishingReturnType diminishingReturn() default DiminishingReturnType.NULL;

    public double priority() default 0.0;
}