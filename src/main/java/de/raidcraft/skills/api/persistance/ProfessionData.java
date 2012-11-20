package de.raidcraft.skills.api.persistance;

import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.util.DataMap;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Set;

/**
 * @author Silthus
 */
public abstract class ProfessionData extends DataMap {

    protected int id;
    protected String name;
    protected String friendlyName;
    protected String description;
    protected boolean active;
    protected boolean mastered;
    protected Set<Skill> skills;
    protected Set<Skill> gainedSkills;
    protected Collection<Profession> strongParents;
    protected Collection<Profession> weakParents;

    public ProfessionData(ResultSet resultSet) throws SQLException {

        this(resultSet, null, null);
    }

    public ProfessionData(ResultSet resultSet, String columnKey, String columnValue) throws SQLException {

        super(resultSet, columnKey, columnValue);
    }

    public ProfessionData(ConfigurationSection config, String... exclude) {

        super(config, exclude);
    }

    public int getId() {

        return id;
    }

    public String getName() {

        return name;
    }

    public String getFriendlyName() {

        return friendlyName;
    }

    public String getDescription() {

        return description;
    }

    public boolean isActive() {

        return active;
    }

    public boolean isMastered() {

        return mastered;
    }

    public Set<Skill> getSkills() {

        return skills;
    }

    public Set<Skill> getGainedSkills() {

        return gainedSkills;
    }

    public Collection<Profession> getStrongParents() {

        return strongParents;
    }

    public Collection<Profession> getWeakParents() {

        return weakParents;
    }
}
