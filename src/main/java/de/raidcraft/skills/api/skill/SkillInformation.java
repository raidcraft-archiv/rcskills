package de.raidcraft.skills.api.skill;

import de.raidcraft.skills.api.combat.EffectElement;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.effect.Effect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Silthus
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SkillInformation {

    String name();

    String description();

    String[] configUsage() default {};

    Class<? extends Effect>[] effects() default {};

    EffectType[] types() default {};

    EffectElement[] elements() default {};

    boolean triggerCombat() default false;

    boolean queuedAttack() default false;
}