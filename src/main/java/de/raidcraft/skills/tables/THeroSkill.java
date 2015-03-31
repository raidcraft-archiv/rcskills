package de.raidcraft.skills.tables;

import com.avaje.ebean.validation.NotNull;
import de.raidcraft.skills.api.persistance.LevelData;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.sql.Timestamp;

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

    private int level;
    private int exp;
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp lastCast;
    private boolean unlocked;
    private Timestamp unlockTime;
    private int castCount;

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
