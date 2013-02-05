package de.raidcraft.skills.tables;

import com.avaje.ebean.validation.NotNull;
import de.raidcraft.skills.api.persistance.ResourceData;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Silthus
 */
@Entity
@Table(name = "skills_hero_resources")
public class THeroResource implements ResourceData {

    @Id
    private int id;

    @NotNull
    private String name;
    private int value;

    @ManyToOne
    private THeroProfession profession;

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

    public int getValue() {

        return value;
    }

    public void setValue(int value) {

        this.value = value;
    }

    public THeroProfession getProfession() {

        return profession;
    }

    public void setProfession(THeroProfession profession) {

        this.profession = profession;
    }
}
