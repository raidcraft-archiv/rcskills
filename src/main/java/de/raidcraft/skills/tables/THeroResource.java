package de.raidcraft.skills.tables;

import com.avaje.ebean.validation.NotNull;
import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
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
    private double value;

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

    public double getValue() {

        return value;
    }

    public void setValue(double value) {

        this.value = value;
    }

    public THeroProfession getProfession() {

        return profession;
    }

    public void setProfession(THeroProfession profession) {

        this.profession = profession;
    }

    public void delete() {

        RaidCraft.getDatabase(SkillsPlugin.class).delete(this);
    }
}
