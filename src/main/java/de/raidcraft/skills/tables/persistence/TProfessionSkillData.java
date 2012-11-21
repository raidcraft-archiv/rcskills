package de.raidcraft.skills.tables.persistence;

import javax.persistence.*;

/**
 * @author Silthus
 */
@Entity
@Table(name = "s_profession_skill_data")
public class TProfessionSkillData {

    @Id
    private int id;

    @ManyToOne
    private TProfessionSkill skill;

    @Column(name = "psd_key")
    private String key;

    @Column(name = "psd_value")
    private String value;

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public TProfessionSkill getSkill() {

        return skill;
    }

    public void setSkill(TProfessionSkill skill) {

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
