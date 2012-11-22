package de.raidcraft.skills.tables;

import com.avaje.ebean.validation.NotNull;
import de.raidcraft.skills.api.persistance.LevelData;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * @author Silthus
 */
public class THeroSkill implements LevelData {

    @Id
    private int id;
    @NotNull
    @Column(unique = true)
    private String name;
    @ManyToOne
    private THeroProfession profession;
    @ManyToOne
    private THero hero;
    private int level;
    private int exp;
    private boolean unlocked;

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public THeroProfession getProfession() {

        return profession;
    }

    public void setProfession(THeroProfession profession) {

        this.profession = profession;
    }

    public THero getHero() {

        return hero;
    }

    public void setHero(THero hero) {

        this.hero = hero;
    }

    public int getLevel() {

        return level;
    }

    public void setLevel(int level) {

        this.level = level;
    }

    public int getExp() {

        return exp;
    }

    @Override
    public int getMaxLevel() {

        throw new UnsupportedOperationException();
    }

    public void setExp(int exp) {

        this.exp = exp;
    }

    public boolean isUnlocked() {

        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {

        this.unlocked = unlocked;
    }
}
