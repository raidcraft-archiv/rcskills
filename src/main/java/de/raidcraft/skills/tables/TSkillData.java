package de.raidcraft.skills.tables;

import com.avaje.ebean.validation.NotNull;
import de.raidcraft.api.database.Bean;

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
    @NotNull
    @ManyToOne
    private THeroSkill skill;
    @NotNull
    private String key;
    @NotNull
    private Object value;


    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public String getKey() {

        return key;
    }

    public void setKey(String key) {

        this.key = key;
    }

    public Object getValue() {

        return value;
    }

    public void setValue(Object value) {

        this.value = value;
    }

    public THeroSkill getSkill() {

        return skill;
    }

    public void setSkill(THeroSkill skill) {

        this.skill = skill;
    }
}
