package de.raidcraft.skills.tables.persistence;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Silthus
 */
@Entity
@Table(name = "s_skill_data")
public class TSkillData {

    @Id
    private int id;

    @ManyToOne
    private TSkill skill;

    private String key;

    private Object value;

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public TSkill getSkill() {

        return skill;
    }

    public void setSkill(TSkill skill) {

        this.skill = skill;
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
}
