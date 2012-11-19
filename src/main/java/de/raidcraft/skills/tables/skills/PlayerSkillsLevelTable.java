package de.raidcraft.skills.tables.skills;

import com.sk89q.commandbook.CommandBook;
import de.raidcraft.api.database.Table;
import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.skills.api.persistance.PlayerSkillLevelData;
import de.raidcraft.skills.api.skill.AbstractLevelableSkill;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Silthus
 */
public class PlayerSkillsLevelTable extends Table {

    public PlayerSkillsLevelTable() {

        super("player_skills_level", "rcskills_");
    }

    @Override
    public void createTable() {

        try {
            getConnection().prepareStatement(
                    "CREATE TABLE `" + getTableName() + "` (\n" +
                            "`id` INT NOT NULL AUTO_INCREMENT ,\n" +
                            "`player` VARCHAR( 32 ) NOT NULL ,\n" +
                            "`skill_id` INT NOT NULL ,\n" +
                            "`level` INT NOT NULL ,\n" +
                            "`exp` INT NOT NULL ,\n" +
                            "`max_exp` INT NOT NULL ,\n" +
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
                    "SELECT COUNT(*) as count FROM `" + getTableName() + "` WHERE player='" + player.getUserName() + "' " +
                            "AND skill_id=" + skillId).executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("count") > 0;
            }
        } catch (SQLException e) {
            CommandBook.logger().severe(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public Data getLevelData(int skillId, RCPlayer player) {

        try {
            ResultSet resultSet = getConnection().prepareStatement(
                    "SELECT * FROM `" + getTableName() + "` WHERE player='" + player.getUserName() + "' AND skill_id=" + skillId).executeQuery();
            if (resultSet.next()) {
                return new Data(resultSet);
            }
        } catch (SQLException e) {
            CommandBook.logger().severe(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public void saveSkillLevel(AbstractLevelableSkill skill) {

        try {
            if (contains(skill.getId(), skill.getPlayer())) {
                // update the table
                getConnection().prepareStatement("UPDATE `" + getTableName() + "` SET " +
                        "level=" + skill.getLevel() + ", " +
                        "exp=" + skill.getExp() + ", " +
                        "max_exp=" + skill.getMaxExp()).executeUpdate();
            } else {
                // insert the skill
                getConnection().prepareStatement("INSERT INTO `" + getTableName() + "` " +
                        "(player, skill_id, level, exp, max_exp) VALUES (" +
                        "'" + skill.getPlayer().getUserName() + "', " +
                        skill.getId() + ", " +
                        skill.getLevel() + ", " +
                        skill.getExp() + ", " +
                        skill.getMaxExp() + ")").executeUpdate();
            }
        } catch (SQLException e) {
            CommandBook.logger().severe(e.getMessage());
            e.printStackTrace();
        }
    }

    public static class Data extends PlayerSkillLevelData {

        private Data(ResultSet resultSet) throws SQLException {

            this.level = resultSet.getInt("level");
            // TODO: get max level from the extra table
            this.maxLevel = 10;
            this.exp = resultSet.getInt("exp");
            this.maxExp = resultSet.getInt("max_exp");
        }
    }
}
