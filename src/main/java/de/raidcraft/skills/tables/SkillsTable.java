package de.raidcraft.skills.tables;

import com.sk89q.commandbook.CommandBook;
import de.raidcraft.api.database.Table;
import de.raidcraft.skills.api.Skill;

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
                            "`cost` DOUBLE NOT NULL ,\n" +
                            "`class` VARCHAR( 128 ) NOT NULL ,\n" +
                            "PRIMARY KEY ( `id` )\n" +
                            ")").execute();
        } catch (SQLException e) {
            CommandBook.logger().severe(e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean contains(int id) {

        try {
            ResultSet resultSet = getConnection().prepareStatement(
                    "SELECT COUNT(*) as count FROM `" + getTableName() + "` WHERE id=" + id).executeQuery();
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
    public Class<? extends Skill> getSkillClass(int id) {

        try {
            ResultSet resultSet = getConnection().prepareStatement(
                    "SELECT class FROM `" + getTableName() + "` WHERE id=" + id).executeQuery();
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

    public Data getSkillData(int id) {

        try {
            ResultSet resultSet = getConnection().prepareStatement(
                    "SELECT * FROM `" + getTableName() + "` WHERE id=" + id).executeQuery();
            if (resultSet.next()) {
                return new Data(id, resultSet);
            }
        } catch (SQLException e) {
            CommandBook.logger().severe(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


    public static class Data {

        public final int id;
        public final String name;
        public final String description;
        public final String[] usage;
        public final double cost;

        private Data(int id, ResultSet resultSet) throws SQLException {

            this.id = id;
            this.name = resultSet.getString("name");
            this.description = resultSet.getString("description");
            this.usage = resultSet.getString("usage").split("\\|");
            this.cost = resultSet.getDouble("cost");
        }
    }
}
