package de.raidcraft.skills.tables.skills;

import com.sk89q.commandbook.CommandBook;
import de.raidcraft.api.database.Database;
import de.raidcraft.api.database.Table;
import de.raidcraft.skills.api.persistance.SkillData;
import de.raidcraft.skills.api.skill.Skill;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Silthus
 */
public class SkillsTable extends Table {

    public SkillsTable() {

        super("skills", "rcskills_");
    }

    @Override
    public void createTable() {

        try {
            getConnection().prepareStatement(
                    "CREATE TABLE `" + getTableName() + "` (\n" +
                            "`id` INT NOT NULL AUTO_INCREMENT ,\n" +
                            "`name` VARCHAR( 128 ) NOT NULL ,\n" +
                            "`description` VARCHAR ( 256 ) NOT NULL ,\n" +
                            "`usage` TEXT NOT NULL ,\n" +
                            "`type` VARCHAR ( 64 ) NOT NULL ,\n" +
                            "`cost` DOUBLE NOT NULL ,\n" +
                            "`needed_level` INT NOT NULL ,\n" +
                            "`require_all_professions` BOOL NOT NULL ,\n" +
                            "`class` VARCHAR( 128 ) NOT NULL ,\n" +
                            "PRIMARY KEY ( `id` )\n" +
                            ")").execute();
        } catch (SQLException e) {
            CommandBook.logger().severe(e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean contains(String id) {

        try {
            ResultSet resultSet = getConnection().prepareStatement(
                    "SELECT COUNT(*) as count FROM `" + getTableName() + "` WHERE name=" + id).executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("count") > 0;
            }
        } catch (SQLException e) {
            CommandBook.logger().severe(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public Class<? extends Skill> getSkillClass(String id) {

        try {
            ResultSet resultSet = getConnection().prepareStatement(
                    "SELECT class FROM `" + getTableName() + "` WHERE name=" + id).executeQuery();
            if (resultSet.next()) {
                return (Class<? extends Skill>) Class.forName(resultSet.getString("class"));
            }
        } catch (SQLException e) {
            CommandBook.logger().severe(e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            CommandBook.logger().severe(e.getMessage());
            e.printStackTrace();
        } catch (ClassCastException e) {
            CommandBook.logger().severe(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public SkillData getSkillData(String id) {

        try {
            ResultSet resultSet = getConnection().prepareStatement(
                    "SELECT * FROM `" + getTableName() + "` s, `" + Database.getTable(SkillsDataTable.class).getTableName() + "` sd" +
                            "WHERE s.id=sd.skill_id AND s.name=" + id).executeQuery();
            if (resultSet.next()) {
                return new Data(resultSet);
            }
        } catch (SQLException e) {
            CommandBook.logger().severe(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static class Data extends SkillData {

        public Data(ResultSet resultSet) throws SQLException {

            super(resultSet, SkillsDataTable.KEY_COLUMN_NAME, SkillsDataTable.VALUE_COLUMN_NAME);
            this.id = resultSet.getInt("id");
            this.description = resultSet.getString("description");
            this.usage = resultSet.getString("usage").split("\\|");
            this.cost = resultSet.getDouble("cost");
            this.requiredLevel = resultSet.getInt("needed_level");
        }
    }

}
