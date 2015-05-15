package de.raidcraft.skills.api.character;

import de.raidcraft.skills.api.ability.Ability;

import java.util.List;

/**
 * @author Silthus
 */
public interface SkilledCharacter<T extends CharacterTemplate> extends CharacterTemplate {

    List<Ability<T>> getAbilties();

    List<Ability<T>> getUseableAbilities();

    void addAbility(Ability<T> ability);

    Ability<T> removeAbility(String name);

    Ability<T> getAbility(String name);
}
