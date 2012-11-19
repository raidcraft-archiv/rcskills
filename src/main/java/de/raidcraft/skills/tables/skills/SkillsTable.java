package de.raidcraft.skills.tables.skills;

import com.sk89q.commandbook.CommandBook;
import de.raidcraft.api.database.Table;
import de.raidcraft.skills.api.Obtainable;
import de.raidcraft.skills.api.persistance.SkillData;
import de.raidcraft.skills.api.skill.Skill;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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

    public SkillData getSkillData(int id) {

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

    public static class Data extends SkillData {

        public Data(int id, ResultSet resultSet) throws SQLException {

            this.id = id;
            this.name = resultSet.getString("name");
            this.description = resultSet.getString("description");
            this.usage = resultSet.getString("usage").split("\\|");
            this.type = Obtainable.Type.fromString(resultSet.getString("type"));
            this.cost = resultSet.getDouble("cost");
            this.neededLevel = resultSet.getInt("needed_level");
            this.allProfessionsRequired = resultSet.getBoolean("require_all_professions");
            this.professions = new ArrayList<>();
            // TODO: load profession -> skill requirements
        }
    }

}
