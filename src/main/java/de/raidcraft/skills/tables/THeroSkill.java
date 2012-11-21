package de.raidcraft.skills.tables;

import com.avaje.ebean.validation.NotNull;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * @author Silthus
 */
public class THeroSkill {

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
    private boolean active;
    private boolean mastered;

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

    public void setExp(int exp) {

        this.exp = exp;
    }

    public boolean isActive() {

        return active;
    }

    public void setActive(boolean active) {

        this.active = active;
    }

    public boolean isMastered() {

        return mastered;
    }

    public void setMastered(boolean mastered) {

        this.mastered = mastered;
    }
}
