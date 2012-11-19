package de.raidcraft.skills;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.LevelData;
import de.raidcraft.skills.api.persistance.ProfessionData;
import de.raidcraft.skills.api.profession.AbstractProfession;
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
public class PlayerProfession extends AbstractProfession {

    private final Hero hero;

    protected PlayerProfession(Hero hero, LevelData levelData, ProfessionData data) {

        super(levelData, data);
        this.hero = hero;
    }

    @Override
    public Hero getHero() {

        return hero;
    }

    @Override
    public void saveLevelProgress() {
        //TODO: implement
    }
}
