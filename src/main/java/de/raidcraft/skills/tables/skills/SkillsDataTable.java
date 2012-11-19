package de.raidcraft.skills.tables.skills;

import com.sk89q.commandbook.CommandBook;
import de.raidcraft.api.database.Table;

import java.sql.SQLException;

/**
 * @author Silthus
 */
public class SkillsDataTable extends Table {

    public static final String KEY_COLUMN_NAME = "key";
    public static final String VALUE_COLUMN_NAME = "value";

    public SkillsDataTable() {

        super("skills_data", "rcskills_");
    }

    @Override
    public void createTable() {

        try {
            getConnection().prepareStatement(
                    "CREATE TABLE `" + getTableName() + "` (\n" +
                            "`id` INT NOT NULL AUTO_INCREMENT ,\n" +
                            "`skill_id` INT NOT NULL ,\n" +
                            "`key` VARCHAR ( 256 ) NOT NULL ,\n" +
                            "`value` VARCHAR ( 256 ) NOT NULL ,\n" +
                            "PRIMARY KEY ( `id` )\n" +
                            ")").execute();
        } catch (SQLException e) {
            CommandBook.logger().severe(e.getMessage());
            e.printStackTrace();
        }
    }
}
