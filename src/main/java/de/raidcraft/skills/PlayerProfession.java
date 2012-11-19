package de.raidcraft.skills;

import de.raidcraft.skills.api.AbstractLevelable;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.LevelData;
import de.raidcraft.skills.api.profession.Profession;

/**
 * Represents a profession instantiated for one {@link RCHero}.
 * Each single {@link de.raidcraft.skills.api.hero.Hero} can have multiple {@link Profession}s at a time. The
 * {@link RCHero} will hold a reference to all {@link PlayerProfession}s obtained by the hero.
 * And the {@link PlayerProfession} will hold a single reference to the corresponding {@link Profession}.
 *
 *
 * @author Silthus
 */
public class PlayerProfession extends AbstractLevelable {

    private final Hero hero;
    private final Profession profession;

    public PlayerProfession(Hero hero, Profession profession, LevelData data) {

        super(data);
        this.hero = hero;
        this.profession = profession;
    }

    @Override
    public Hero getHero() {

        return hero;
    }

    public Profession getProfession() {

        return profession;
    }

    @Override
    public void saveLevelProgress() {
        //TODO: implement
    }
}
