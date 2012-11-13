package de.raidcraft.skills.tables.professions;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.database.Table;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.skill.Skill;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Silthus
 */
public class ProfessionsTable extends Table {

    public ProfessionsTable() {

        super("professions", "rcskills_");
    }

    @Override
    public void createTable() {
        //TODO: implement
    }

    public Data getProfessionData(int id) throws UnknownProfessionException {

        try {
            ResultSet resultSet = getConnection().prepareStatement("SELECT * FROM `" + getTableName() + "` WHERE id=" + id).executeQuery();
            if (resultSet.next()) {
                return new Data(id, resultSet);
            }
        } catch (SQLException e) {
            RaidCraft.LOGGER.severe(e.getMessage());
            e.printStackTrace();
        }
        throw new UnknownProfessionException("Es gibt keinen Beruf oder Klasse mit der ID: " + id);
    }


    public static class Data {

        public final int id;
        public final String name;
        public final String description;
        public final Collection<Skill> skills;

        public Data(int id, ResultSet resultSet) throws SQLException {

            this.id = id;
            this.name = resultSet.getString("name");
            this.description = resultSet.getString("description");
            this.skills = new ArrayList<>();
            // TODO: load skills of this profession
        }
    }
}
