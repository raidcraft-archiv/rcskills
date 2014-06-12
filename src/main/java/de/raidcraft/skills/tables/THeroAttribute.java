package de.raidcraft.skills.tables;

import de.raidcraft.api.items.AttributeType;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Silthus
 */
@Entity
@Table(name = "skills_attributes")
public @Data class THeroAttribute {

    @Id
    private int id;

    @ManyToOne
    private THero hero;

    private AttributeType attribute;
    private int baseValue;
    private int currentValue;

}
