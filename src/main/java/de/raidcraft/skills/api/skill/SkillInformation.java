package de.raidcraft.skills.api.skill;

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
public @interface SkillInformation {

    public String name();

    public String desc();

    public EffectType[] types();

    public EffectElement[] elements() default {};

    public boolean triggerCombat();
}
