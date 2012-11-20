package de.raidcraft.skills.tables.skills;

import com.sk89q.commandbook.CommandBook;
import de.raidcraft.api.database.Table;
import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.LevelData;
import de.raidcraft.skills.api.skill.LevelableSkill;

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
                            "PRIMARY KEY ( `id` )\n" +
                            ")").execute();
        } catch (SQLException e) {
            CommandBook.logger().severe(e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean contains(String skillId, Hero player) {

        try {
            ResultSet resultSet = getConnection().prepareStatement(
                    "SELECT COUNT(*) as count FROM `" + getTableName() + "` WHERE player='" + player.getName() + "' " +
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

    public void saveSkillLevel(LevelableSkill skill) {

        try {
            if (contains(skill.getName(), skill.getHero())) {
                // update the table
                getConnection().prepareStatement("UPDATE `" + getTableName() + "` SET " +
                        "level=" + skill.getLevel() + ", " +
                        "exp=" + skill.getLevel().getExp()).executeUpdate();
            } else {
                // insert the skill
                getConnection().prepareStatement("INSERT INTO `" + getTableName() + "` " +
                        "(player, skill_id, level, exp) VALUES (" +
                        "'" + skill.getHero().getName() + "', " +
                        skill.getId() + ", " +
                        skill.getLevel().getLevel() + ", " +
                        skill.getLevel().getExp() + ")").executeUpdate();
            }
        } catch (SQLException e) {
            CommandBook.logger().severe(e.getMessage());
            e.printStackTrace();
        }
    }

    public static class Data extends LevelData {

        private Data(ResultSet resultSet) throws SQLException {

            super(resultSet);
            this.level = resultSet.getInt("level");
            this.exp = resultSet.getInt("exp");
            // TODO: get the max level from the skill config
        }
    }
}
