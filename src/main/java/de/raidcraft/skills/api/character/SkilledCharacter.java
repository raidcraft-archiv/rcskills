package de.raidcraft.skills.api.character;

import de.raidcraft.skills.api.skill.Ability;

import java.util.Collection;

/**
 * @author Silthus
 */
public interface SkilledCharacter<T extends Ability<? extends SkilledCharacter>> extends CharacterTemplate {

    public Collection<T> getAbilties();

    public void addAbility(T ability);

    public T removeAbility(String name);

    public T getAbility(String name);
}
