package de.raidcraft.skills.api.persistance;

import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.util.DataMap;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
* @author Silthus
*/
public abstract class SkillData extends DataMap {

    protected int id;
    protected String friendlyName;
    protected String[] usage;
    protected double cost;
    protected int neededLevel;
    protected Collection<Profession> professions;
    protected boolean allProfessionsRequired;
    protected Collection<Skill> strongParents;
    protected Collection<Skill> weakParents;

    public SkillData(ResultSet resultSet) throws SQLException {

        this(resultSet, null, null);
    }

    public SkillData(ResultSet resultSet, String columnKey, String columnValue) throws SQLException {

        super(resultSet, columnKey, columnValue);
    }

    public SkillData(ConfigurationSection config, String... exclude) {

        super(config, exclude);
    }

    public int getId() {

        return id;
    }

    public String getFriendlyName() {

        return friendlyName;
    }

    public String[] getUsage() {

        return usage;
    }

    public double getCost() {

        return cost;
    }

    public int getNeededLevel() {

        return neededLevel;
    }

    public Collection<Profession> getProfessions() {

        return professions;
    }

    public boolean isAllProfessionsRequired() {

        return allProfessionsRequired;
    }

    public Collection<Skill> getStrongParents() {

        return strongParents;
    }

    public Collection<Skill> getWeakParents() {

        return weakParents;
    }
}
