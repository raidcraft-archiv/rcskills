package de.raidcraft.skills.tables;

import javax.persistence.*;

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

    @Column(name = "sd_key")
    private String key;

    @Column(name = "sd_value")
    private String value;

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

    public String getValue() {

        return value;
    }

    public void setValue(String value) {

        this.value = value;
    }
}
