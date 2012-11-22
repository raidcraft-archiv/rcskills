package de.raidcraft.skills.professions;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.Level;
import de.raidcraft.skills.api.persistance.ProfessionData;
import de.raidcraft.skills.api.profession.AbstractProfession;
import de.raidcraft.skills.api.profession.Profession;

/**
 * Represents a profession instantiated for one {@link Hero}.
 * Each single {@link Hero} can have multiple {@link Profession}s at a time. The
 * {@link Hero} will hold a reference to all {@link Profession}s obtained by the hero.
 *
 * @author Silthus
 */
public class SimpleProfession extends AbstractProfession {


    public SimpleProfession(Hero hero, ProfessionData data) {

        super(hero, data);
        attachLevel(new ProfessionLevel(this, data.getLevelData()));
    }

    @Override
    public void increaseLevel(Level level) {
        //TODO: implement
    }

    @Override
    public void decreaseLevel(Level level) {
        //TODO: implement
    }

    @Override
    public void saveLevelProgress(Level level) {
        //TODO: implement
    }
}
