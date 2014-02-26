package de.raidcraft.skills.api.skill;

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
public @interface SkillInformation {

    public String name();

    public String description();
    
    public String[] configUseage() default {};

    public EffectType[] types() default {};

    public EffectElement[] elements() default {};

    public boolean triggerCombat() default false;

    public boolean queuedAttack() default false;
}