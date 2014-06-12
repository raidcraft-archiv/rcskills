package de.raidcraft.skills.tables;

import com.avaje.ebean.validation.NotNull;
import de.raidcraft.skills.api.persistance.ResourceData;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Silthus
 */
@Entity
@Table(name = "skills_hero_resources")
public @Data class THeroResource implements ResourceData {

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
