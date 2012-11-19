package de.raidcraft.skills.api.persistance;

import de.raidcraft.util.DataMap;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Silthus
 */
public abstract class LevelData extends DataMap {

    protected int level;
    protected int maxLevel;
    protected int exp;

    public LevelData(ResultSet resultSet) throws SQLException {

        this(resultSet, null, null);
    }

    public LevelData(ResultSet resultSet, String columnKey, String columnValue) throws SQLException {

        super(resultSet, columnKey, columnValue);
    }

    public LevelData(ConfigurationSection config, String... exclude) {

        super(config, exclude);
    }

    public int getLevel() {

        return level;
    }

    public int getMaxLevel() {

        return maxLevel;
    }

    public int getExp() {

        return exp;
    }
}
