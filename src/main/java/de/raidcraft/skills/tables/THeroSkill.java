package de.raidcraft.skills.tables;

import de.raidcraft.skills.api.persistance.LevelData;
import io.ebean.annotation.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
@Entity
@Table(name = "rc_skills_hero_skills")
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

    private int level;
    private int exp;
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp lastCast;
    private boolean unlocked;
    private Timestamp unlockTime;
    private int castCount;
    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "skill_id")
    private List<TSkillData> skillDatas = new ArrayList<>();

    public void setLastCast(Timestamp lastCast) {

        this.lastCast = lastCast;
        setCastCount(getCastCount() + 1);
    }

    @Override
    public int getLevel() {

        return level;
    }

    @Override
    public int getExp() {

        return exp;
    }
}
