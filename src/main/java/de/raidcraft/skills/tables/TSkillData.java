package de.raidcraft.skills.tables;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Silthus
 */
@Entity
@Table(name = "rc_skills_skill_data")
@Getter
@Setter
public class TSkillData {

    @Id
    private int id;

    @ManyToOne
    private THeroSkill skill;

    private String dataKey;
    private String dataValue;
}
