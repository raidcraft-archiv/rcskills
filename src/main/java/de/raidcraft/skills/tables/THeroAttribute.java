package de.raidcraft.skills.tables;

import de.raidcraft.api.database.Bean;
import de.raidcraft.api.items.AttributeType;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Silthus
 */
@Entity
@Table(name = "skills_attributes")
public class THeroAttribute implements Bean {

    @Id
    private int id;
    @ManyToOne
    private THero hero;
    private AttributeType attribute;
    private int baseValue;
    private int currentValue;

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

    public AttributeType getAttribute() {

        return attribute;
    }

    public void setAttribute(AttributeType attribute) {

        this.attribute = attribute;
    }

    public int getBaseValue() {

        return baseValue;
    }

    public void setBaseValue(int baseValue) {

        this.baseValue = baseValue;
    }

    public int getCurrentValue() {

        return currentValue;
    }

    public void setCurrentValue(int currentValue) {

        this.currentValue = currentValue;
    }
}
