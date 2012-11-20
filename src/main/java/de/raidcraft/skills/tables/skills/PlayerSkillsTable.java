package de.raidcraft.skills.tables.skills;

import com.sk89q.commandbook.CommandBook;
import de.raidcraft.api.database.Database;
import de.raidcraft.api.database.Table;
import de.raidcraft.api.player.RCPlayer;

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

    public boolean contains(String skillId, RCPlayer player) {

        try {
            ResultSet resultSet = getConnection().prepareStatement(
                    "SELECT COUNT(*) as count FROM `" + getTableName() + "` ps, `" + Database.getTable(SkillsTable.class).getTableName() + "` s" +
                            "WHERE s.id=ps.skill_id AND s.name='" + skillId + "' " +
                            "AND ps.player='" + player.getUserName() + "'").executeQuery();
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
