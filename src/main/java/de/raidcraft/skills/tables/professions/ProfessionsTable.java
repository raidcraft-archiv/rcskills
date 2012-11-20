package de.raidcraft.skills.tables.professions;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.database.Table;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.persistance.ProfessionData;

import java.sql.ResultSet;
import java.sql.SQLException;

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

    public Data getProfessionData(String id) throws UnknownProfessionException {

        try {
            ResultSet resultSet = getConnection().prepareStatement("SELECT * FROM `" + getTableName() + "` WHERE name=" + id).executeQuery();
            if (resultSet.next()) {
                return new Data(resultSet);
            }
        } catch (SQLException e) {
            RaidCraft.LOGGER.severe(e.getMessage());
            e.printStackTrace();
        }
        throw new UnknownProfessionException("Es gibt keinen Beruf oder Klasse mit der ID: " + id);
    }


    public static class Data extends ProfessionData {

        public Data(ResultSet resultSet) throws SQLException {

            super(resultSet);
            this.id = resultSet.getInt("id");
            this.name = resultSet.getString("name");
            this.friendlyName = resultSet.getString("friendly_name");
            this.description = resultSet.getString("description");
            // TODO: load skills of this profession
        }
    }
}
