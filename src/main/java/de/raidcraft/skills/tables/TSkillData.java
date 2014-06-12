package de.raidcraft.skills.tables;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Silthus
 */
@Entity
@Table(name = "skills_skill_data")
public @Data class TSkillData {

    @Id
    private int id;

    @ManyToOne
    private THeroSkill skill;

    private String dataKey;
    private String dataValue;

}
