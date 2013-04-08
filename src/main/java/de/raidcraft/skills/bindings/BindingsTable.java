package de.raidcraft.skills.bindings;

import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.database.Table;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.Material;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Philip
 */
public class BindingsTable extends Table {

    public BindingsTable() {

        super("bindings", "skills_");
    }

    @Override
    public void createTable() {

        try {
            getConnection().prepareStatement(
                    "CREATE TABLE `" + getTableName() + "` (\n" +
                            "`id` INT NOT NULL AUTO_INCREMENT ,\n" +
                            "`player` VARCHAR ( 32 ) NOT NULL ,\n" +
                            "`item` VARCHAR ( 32 ) NOT NULL ,\n" +
                            "`skill` VARCHAR ( 32 ) NOT NULL ,\n" +
                            "`args` VARCHAR ( 64 ) NOT NULL ,\n" +
                            "PRIMARY KEY ( `id` )\n" +
                            ")").execute();
        } catch (SQLException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
        }
    }

    public List<Binding> getBindings(Hero hero) {

        List<Binding> bindings = new ArrayList<>();

        try {
            ResultSet resultSet = getConnection().prepareStatement(
                    "SELECT * FROM " + getTableName() + " WHERE player = '" + hero.getName() + "';").executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                Material material = Material.getMaterial(resultSet.getString("item"));
                if (material == null) {
                    deleteBinding(id);
                    continue;
                }
                Skill skill;
                CommandContext args;
                try {
                    skill = hero.getSkill(resultSet.getString("skill"));
                    args = new CommandContext(resultSet.getString("args"));
                } catch (UnknownSkillException | CommandException e) {
                    deleteBinding(id);
                    continue;
                }
                bindings.add(new Binding(hero, material, skill, args));
            }
        } catch (SQLException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
        }
        return bindings;
    }

    public void saveBinding(Binding binding) {

        try {
            getConnection().prepareStatement(
                    "DELETE FROM " + getTableName() + " WHERE " +
                            "player = '" + binding.getHero().getName() + "' AND " +
                            "item = '" + binding.getMaterial().name() + "' AND " +
                            "skill = '" + binding.getSkill().getName() + "';").execute();
            String args = "";
            if (binding.getArgs() != null && binding.getArgs().argsLength() > 0) {
                args = binding.getArgs().getJoinedStrings(0);
            }
            getConnection().prepareStatement("INSERT INTO " + getTableName() + " (player, item, skill, args) " +
                    "VALUES (" +
                    "'" + binding.getHero().getName() + "'" + "," +
                    "'" + binding.getMaterial().name() + "'" + "," +
                    "'" + binding.getSkill().getName() + "'" + "," +
                    "'" + args + "'" +
                    ")").executeUpdate();
        } catch (SQLException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
            e.printStackTrace();
        }
    }

    public void deleteBinding(Hero hero, Material material, Skill skill) {

        try {
            getConnection().prepareStatement(
                    "DELETE FROM " + getTableName() + " WHERE " +
                            "player = '" + hero.getName() + "' AND " +
                            "item = '" + material.name() + "' AND " +
                            "skill = '" + skill.getName() + "';").execute();
        } catch (SQLException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
            e.printStackTrace();
        }
    }

    public void deleteBinding(Binding binding) {

        deleteBinding(binding.getHero(), binding.getMaterial(), binding.getSkill());
    }

    public void deleteBinding(int id) {

        try {
            getConnection().prepareStatement(
                    "DELETE FROM " + getTableName() + " WHERE id = '" + id + "';").execute();
        } catch (SQLException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
            e.printStackTrace();
        }
    }
}
