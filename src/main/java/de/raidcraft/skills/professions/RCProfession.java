package de.raidcraft.skills.professions;

import de.raidcraft.api.database.Database;
import de.raidcraft.skills.api.level.Level;
import de.raidcraft.skills.api.level.SimpleLevel;
import de.raidcraft.skills.api.exceptions.UnknownPlayerProfessionException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.ProfessionData;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.tables.professions.PlayerProfessionsTable;

/**
 * Represents a profession instantiated for one {@link Hero}.
 * Each single {@link Hero} can have multiple {@link Profession}s at a time. The
 * {@link Hero} will hold a reference to all {@link Profession}s obtained by the hero.
 *
 * @author Silthus
 */
public class RCProfession extends AbstractLevelableProfession {


    protected RCProfession(Hero hero, ProfessionData data) throws UnknownPlayerProfessionException {

        super(hero, data);
        // attach a new level object that loads from the db
        attachLevel(new SimpleLevel<LevelableProfession>(this,
                Database.getTable(PlayerProfessionsTable.class).getLevelData(hero, data.getId())));
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
