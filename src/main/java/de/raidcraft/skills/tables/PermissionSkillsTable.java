package de.raidcraft.skills.tables;

import com.sk89q.commandbook.CommandBook;
import de.raidcraft.componentutils.database.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author Silthus
 */
public class PermissionSkillsTable extends Table {

    public PermissionSkillsTable() {

        super("permission_skills", "rcskills_");
    }

    @Override
    public void createTable() {

        try {
            getConnection().prepareStatement(
                    "CREATE TABLE `" + getTableName() + "` (\n" +
                            "`id` INT NOT NULL AUTO_INCREMENT ,\n" +
                            "`skill_id` INT NOT NULL ,\n" +
                            "`permission` VARCHAR( 64 ) NULL ,\n" +
                            "`group` VARCHAR( 64 ) NULL ,\n" +
                            "PRIMARY KEY ( `id` )\n" +
                            ")").execute();
        } catch (SQLException e) {
            CommandBook.logger().severe(e.getMessage());
            e.printStackTrace();
        }
    }

    public Data getPermissionsData(int skillId) {

        try {
            ResultSet resultSet = getConnection().prepareStatement(
                    "SELECT * FROM `" + getTableName() + "` WHERE skill_id=" + skillId).executeQuery();
            if (resultSet.next()) {
                return new Data(resultSet);
            }
        } catch (SQLException e) {
            CommandBook.logger().severe(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static class Data {

        public final Collection<String> permissions;
        public final Collection<String> groups;

        private Data(ResultSet resultSet) throws SQLException {

            this.permissions = new HashSet<>();
            this.groups = new HashSet<>();

            String permission;
            String group;

            do {
                permission = resultSet.getString("permission");
                if (permission != null && !permission.equals("")) {
                    permissions.add(permission);
                }

                group = resultSet.getString("group");
                if (group != null && !group.equals("")) {
                    groups.add(group);
                }
            } while (resultSet.next());
        }
    }
}
