package de.raidcraft.skills.api.traits;

import de.raidcraft.skills.api.character.CharacterTemplate;

public interface CharacterTraitRegistry {

    /**
     * Adds all default traits to a given CharacterTemplate.
     *
     * @param character The {@link CharacterTemplate} to add default traits to
     */
    void addDefaultTraits(CharacterTemplate character);

    /**
     * Removes a trait. This prevents a trait from being added to an CharacterTemplate but does not remove existing traits from the
     * CharacterTemplates.
     *
     * @param info The TraitInfo to deregister
     */
    void deregisterTrait(CharacterTraitFactory info);

    /**
     * Gets a trait with the given class.
     *
     * @param clazz Class of the trait
     * @return Trait with the given class
     */
    <T extends CharacterTrait> T getTrait(Class<T> clazz);

    /**
     * Gets a trait with the given name.
     *
     * @param name Name of the trait
     * @return Trait with the given name
     */
    <T extends CharacterTrait> T getTrait(String name);

    /**
     * Gets the {@link CharacterTrait} class with the given name, or null if not found.
     *
     * @param name The trait name
     * @return The trait class
     */
    Class<? extends CharacterTrait> getTraitClass(String name);

    /**
     * Checks whether the given trait is 'internal'. An internal trait is implementation-defined and is default or
     * built-in.
     *
     * @param trait The trait to check
     * @return Whether the trait is an internal trait
     */
    boolean isInternalTrait(CharacterTrait trait);

    /**
     * Registers a trait using the given information.
     *
     * @param info Registration information
     */
    void registerTrait(CharacterTraitFactory info);
}
