package de.raidcraft.skills.tables;

import com.sk89q.commandbook.CommandBook;
import de.raidcraft.componentutils.database.Table;
import de.raidcraft.rcrpg.api.player.RCPlayer;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Silthus
 */
public class PlayerSkillsTable extends Table {

    public PlayerSkillsTable() {

        super("player_skills", "rcskills_");
    }

    @Override
    public void createTable() {

        try {
            getConnection().prepareStatement(
                    "CREATE TABLE `" + getTableName() + "` (\n" +
                            "`id` INT NOT NULL AUTO_INCREMENT ,\n" +
                            "`player` VARCHAR( 32 ) NOT NULL ,\n" +
                            "`skill_id` INT NOT NULL ,\n" +
                            "`bought` BOOL NOT NULL ,\n" +
                            "`gained` LONG NOT NULL ,\n" +
                            "PRIMARY KEY ( `id` )\n" +
                            ")").execute();
        } catch (SQLException e) {
            CommandBook.logger().severe(e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean contains(int skillId, RCPlayer player) {

        try {
            ResultSet resultSet = getConnection().prepareStatement(
                    "SELECT COUNT(*) as count FROM `" + getTableName() + "` WHERE skill_id=" + skillId + " " +
                            "AND player='" + player.getUserName() + "'").executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("count") > 0;
            }
        } catch (SQLException e) {
            CommandBook.logger().severe(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
