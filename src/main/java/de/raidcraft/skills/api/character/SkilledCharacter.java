package de.raidcraft.skills.api.character;

import de.raidcraft.skills.api.ability.Ability;

import java.util.Collection;

/**
 * @author Silthus
 */
public interface SkilledCharacter<T extends CharacterTemplate> extends CharacterTemplate {

    public Collection<Ability<T>> getAbilties();

    public void addAbility(Ability<T> ability);

    public Ability<T> removeAbility(String name);

    public Ability<T> getAbility(String name);
}
