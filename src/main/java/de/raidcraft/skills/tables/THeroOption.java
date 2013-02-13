package de.raidcraft.skills.tables;

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
    private THero hero;
    private String optionKey;
    private boolean optionValue;

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

    public String getOptionKey() {

        return optionKey;
    }

    public void setOptionKey(String optionKey) {

        this.optionKey = optionKey;
    }

    public boolean isOptionValue() {

        return optionValue;
    }

    public void setOptionValue(boolean optionValue) {

        this.optionValue = optionValue;
    }
}
