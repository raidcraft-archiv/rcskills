package de.raidcraft.skills.tables;

import de.raidcraft.skills.api.persistance.ResourceData;
import io.ebean.annotation.NotNull;
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
@Table(name = "rc_skills_hero_resources")
@Getter
@Setter
public class THeroResource implements ResourceData {

    @Id
    private int id;

    @NotNull
    private String name;
    private double value;

    @ManyToOne
    private THeroProfession profession;

    @Override
    public int getId() {

        return id;
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public double getValue() {

        return value;
    }

    @Override
    public void setValue(double value) {

        this.value = value;
    }
}
