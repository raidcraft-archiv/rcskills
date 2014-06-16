package de.raidcraft.skills.tables;

import com.avaje.ebean.validation.NotNull;
import de.raidcraft.skills.api.persistance.LevelData;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author Silthus
 */
@Entity
@Table(name = "skills_hero_skills")
@Getter
@Setter
public class THeroSkill implements LevelData {

    @Id
    private int id;

    @NotNull
    private String name;

    @ManyToOne
    private THeroProfession profession;

    @ManyToOne
    private THero hero;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "skill_id")
    private List<TSkillData> skillData;

    private int level;
    private int exp;
    private boolean unlocked;
    private Timestamp unlockTime;

    @Override
    public int getLevel() {

        return level;
    }

    @Override
    public int getExp() {

        return exp;
    }
}
