package de.raidcraft.skills.tables;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.database.Bean;
import de.raidcraft.skills.SkillsPlugin;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Silthus
 */
@Entity
@Table(name = "skills_skill_data")
public class TSkillData implements Bean {

    @Id
    private int id;
    @ManyToOne
    private THeroSkill skill;
    private String dataKey;
    private String dataValue;

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public String getDataKey() {

        return dataKey;
    }

    public void setDataKey(String dataKey) {

        this.dataKey = dataKey;
    }

    public String getDataValue() {

        return dataValue;
    }

    public void setDataValue(String dataValue) {

        this.dataValue = dataValue;
    }

    public THeroSkill getSkill() {

        return skill;
    }

    public void setSkill(THeroSkill skill) {

        this.skill = skill;
    }

    public void delete() {

        RaidCraft.getDatabase(SkillsPlugin.class).delete(this);
    }
}
