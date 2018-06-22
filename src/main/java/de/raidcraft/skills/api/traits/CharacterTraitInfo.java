package de.raidcraft.skills.api.traits;

import de.raidcraft.skills.api.character.CharacterTemplate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CharacterTraitInfo {
    String value();

    Class<? extends CharacterTemplate> characterClass();

    String description() default "";
}