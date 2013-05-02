package de.raidcraft.skills.api.character;

import de.raidcraft.skills.api.ability.Ability;

import java.util.List;

/**
 * @author Silthus
 */
public interface SkilledCharacter<T extends CharacterTemplate> extends CharacterTemplate {

    public List<Ability<T>> getAbilties();

    public List<Ability<T>> getUseableAbilities();

    public void addAbility(Ability<T> ability);

    public Ability<T> removeAbility(String name);

    public Ability<T> getAbility(String name);
}
