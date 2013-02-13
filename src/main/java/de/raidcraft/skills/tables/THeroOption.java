package de.raidcraft.skills.tables;

import com.avaje.ebean.validation.NotNull;
import de.raidcraft.api.database.Bean;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Silthus
 */
@Entity
@Table(name = "skills_hero_options")
public class THeroOption implements Bean {

    @Id
    private int id;
    @ManyToOne
    @NotNull
    private THero hero;
    @NotNull
    private String option;
    @NotNull
    private boolean value;

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public THero getHero() {

        return hero;
    }

    public void setHero(THero hero) {

        this.hero = hero;
    }

    public String getOption() {

        return option;
    }

    public void setOption(String option) {

        this.option = option;
    }

    public boolean isValue() {

        return value;
    }

    public void setValue(boolean value) {

        this.value = value;
    }
}
