package de.raidcraft.skills.api.traits;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.trigger.Triggered;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;

public abstract class CharacterTrait<TCharacter extends CharacterTemplate> implements Triggered, Listener, Runnable {

    private final String name;
    protected TCharacter character = null;
    private boolean runImplemented = true;

    protected CharacterTrait(String name) {
        this.name = name.toLowerCase();
    }

    /**
     * Gets the name of this trait.
     *
     * @return Name of this trait
     */
    public final String getName() {
        return name;
    }

    /**
     * @return The {@link CharacterTemplate} this trait is attached to. May be null.
     */
    public final TCharacter getCharacter() {
        return character;
    }

    public boolean isRunImplemented() {
        run();
        return runImplemented;
    }

    public void linkToCharacter(TCharacter character) {
        if (this.character != null)
            throw new IllegalArgumentException("character may only be set once");
        this.character = character;
        onAttach();
    }

    /**
     * Loads a trait.
     *
     * @param config {@link ConfigurationSection} to load from
     */
    public void load(ConfigurationSection config) {
    }

    /**
     * Called when the trait has been attached to an {@link CharacterTemplate}.
     * {@link #character} will be null until this is called.
     */
    public void onAttach() {
    }

    /**
     * Called when the trait has been newly copied to an {@link CharacterTemplate}.
     */
    public void onCopy() {
    }

    /**
     * Called just before the attached {@link CharacterTemplate} is despawned. {@link CharacterTemplate#getEntity()} will be non-null.
     */
    public void onDespawn() {
    }

    /**
     * Called when a trait is removed from the attached {@link CharacterTemplate}.
     */
    public void onRemove() {
    }

    /**
     * Called when an {@link CharacterTemplate} is spawned. {@link CharacterTemplate#getEntity()} will return null until this is called. This is
     * also called onAttach when the Character is already spawned.
     * For {@link org.bukkit.entity.Player}s this will be called when they join the server and their "Profile" is loaded.
     */
    public void onSpawn() {
    }

    @Override
    public void run() {
        runImplemented = false;
    }

    /**
     * Saves a trait.
     *
     * @param config {@link ConfigurationSection} to save to
     */
    public void save(ConfigurationSection config) {
    }
}
