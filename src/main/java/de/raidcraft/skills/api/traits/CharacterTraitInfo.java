package de.raidcraft.skills.api.traits;

import com.google.common.base.Preconditions;

import java.util.function.Supplier;

/**
 * Builds a trait.
 */
public final class CharacterTraitInfo {
    private boolean defaultTrait;
    private String name;
    private Supplier<? extends CharacterTrait> supplier;
    private final Class<? extends CharacterTrait> trait;
    private boolean triedAnnotation;

    private CharacterTraitInfo(Class<? extends CharacterTrait> trait) {
        this.trait = trait;
    }

    public CharacterTraitInfo asDefaultTrait() {
        this.defaultTrait = true;
        return this;
    }

    public Class<? extends CharacterTrait> getTraitClass() {
        return trait;
    }

    public String getTraitName() {
        if (name == null && !triedAnnotation) {
            CharacterTraitInformation anno = trait.getAnnotation(CharacterTraitInformation.class);
            if (anno != null) {
                name = anno.value();
            }
            triedAnnotation = true;
        }
        return name;
    }

    public boolean isDefaultTrait() {
        return defaultTrait;
    }

    @SuppressWarnings("unchecked")
    public <T extends CharacterTrait> T tryCreateInstance() {
        if (supplier != null)
            return (T) supplier.get();
        try {
            return (T) trait.newInstance();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public CharacterTraitInfo withName(String name) {
        Preconditions.checkNotNull(name);
        this.name = name.toLowerCase();
        return this;
    }

    public CharacterTraitInfo withSupplier(Supplier<? extends CharacterTrait> supplier) {
        this.supplier = supplier;
        return this;
    }

    /**
     * Constructs a factory with the given trait class. The trait class must have a no-arguments constructor.
     *
     * @param trait Class of the trait
     * @return The created {@link CharacterTraitInfo}
     * @throws IllegalArgumentException If the trait class does not have a no-arguments constructor
     */
    public static CharacterTraitInfo create(Class<? extends CharacterTrait> trait) {
        Preconditions.checkNotNull(trait);
        try {
            trait.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Trait class must have a no-arguments constructor");
        }
        return new CharacterTraitInfo(trait);
    }
}