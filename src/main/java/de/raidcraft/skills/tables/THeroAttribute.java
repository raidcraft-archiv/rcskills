package de.raidcraft.skills.tables;

import de.raidcraft.api.items.AttributeType;
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
@Table(name = "rc_skills_attributes")
@Getter
@Setter
public class THeroAttribute {

    @Id
    private int id;

    @ManyToOne
    private THero hero;

    private AttributeType attribute;
    private int baseValue;
    private int currentValue;

}
