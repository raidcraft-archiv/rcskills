package de.raidcraft.skills.tables;

import com.sk89q.commandbook.CommandBook;
import de.raidcraft.api.database.Table;

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
                            "PRIMARY KEY ( `id` )\n" +
                            ")").execute();
        } catch (SQLException e) {
            CommandBook.logger().severe(e.getMessage());
            e.printStackTrace();
        }
    }
}
