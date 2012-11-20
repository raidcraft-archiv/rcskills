package de.raidcraft.skills.tables;

import com.sk89q.commandbook.CommandBook;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.database.Table;
import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.persistance.HeroData;
import de.raidcraft.skills.api.persistance.LevelData;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Silthus
 */
public class PlayerTable extends Table {

    public PlayerTable() {

        super("players", "rcskills_");
    }

    @Override
    public void createTable() {

        try {
            getConnection().prepareStatement(
                    "CREATE TABLE `" + getTableName() + "` (\n" +
                            "`id` INT NOT NULL AUTO_INCREMENT ,\n" +
                            "`player` VARCHAR( 64 ) UNIQUE NOT NULL ,\n" +
                            "`level` INT NOT NULL ,\n" +
                            "`exp` INT NOT NULL ,\n" +
                            "PRIMARY KEY ( `id` )\n" +
                            ")").execute();
        } catch (SQLException e) {
            CommandBook.logger().severe(e.getMessage());
            e.printStackTrace();
        }
    }

    public LevelData getLevelData(String player) throws UnknownPlayerException {

        try {
            ResultSet resultSet = getConnection().prepareStatement(
                    "SELECT * FROM `" + getTableName() + "` WHERE player='" + player + "'").executeQuery();
            if (resultSet.next()) {
                return new PlayerLevelData(resultSet);
            }
        } catch (SQLException e) {
            CommandBook.logger().severe(e.getMessage());
            e.printStackTrace();
        }
        throw new UnknownPlayerException("Es gibt keinen Spieler mit dem Namen: " + player);
    }

    public HeroData getHeroData(String name) throws UnknownPlayerException, UnknownProfessionException {

        try {
            ResultSet resultSet = getConnection().prepareStatement(
                    "SELECT COUNT(*) as count, * FROM `" + getTableName() + "´ WHERE player IS LIKE '" + name + "%'").executeQuery();
            if (resultSet.next()) {
                if (resultSet.getInt("count") > 1)
                    throw new UnknownPlayerException("Es gibt mehrere Spieler mit dem Namen " + name + " in der Datenbank.");
                return new PlayerData(resultSet);
            }
        } catch (SQLException e) {
            CommandBook.logger().severe(e.getMessage());
            e.printStackTrace();
        }
        throw new UnknownPlayerException("Es gibt keinen Spieler mit dem Namen: " + name);
    }

    public static class PlayerData extends HeroData {

        public PlayerData(ResultSet resultSet) throws SQLException, UnknownProfessionException {

            this.player = RaidCraft.getPlayer(resultSet.getString("player"));
            this.selectedProfession = RaidCraft.getComponent(SkillsPlugin.class).getProfessionManager()
                    .getProfession(resultSet.getString("selected_profession"));
        }
    }

    public static class PlayerLevelData extends LevelData {


        public PlayerLevelData(ResultSet resultSet) throws SQLException {

            super(resultSet);
            this.exp = resultSet.getInt("exp");
            this.level = resultSet.getInt("level");
            this.maxLevel = RaidCraft.getComponent(SkillsPlugin.class).getLocalConfiguration().player_max_level;
        }
    }
}
