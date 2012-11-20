package de.raidcraft.skills.tables.professions;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.database.Table;
import de.raidcraft.skills.api.exceptions.UnknownPlayerProfessionException;import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.LevelData;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Silthus
 */
public class PlayerProfessionsTable extends Table {

    public PlayerProfessionsTable() {

        super("player_professions", "rcskills_");
    }

    @Override
    public void createTable() {
        //TODO: implement
    }

    public LevelData getLevelData(Hero hero, int professionId)throws UnknownPlayerProfessionException {

        try {
            ResultSet resultSet = getConnection().prepareStatement(
                    "SELECT * FROM `" + getTableName() + "` WHERE player='" + hero.getName() + "' AND pid=" + professionId).executeQuery();
            if (resultSet.next()) {
                return new Data(resultSet);
            }
        } catch (SQLException e) {
            RaidCraft.LOGGER.severe(e.getMessage());
            e.printStackTrace();
        }
        throw new UnknownPlayerProfessionException("Der Spieler hat diese Spezialisierung noch nicht. PID: " + professionId);
    }

    public static class Data extends LevelData {

        public Data(ResultSet resultSet) throws SQLException {

            super(resultSet);
        }
    }
}
