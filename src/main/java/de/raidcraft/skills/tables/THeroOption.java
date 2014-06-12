package de.raidcraft.skills.tables;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Silthus
 */
@Entity
@Table(name = "skills_hero_options")
public @Data class THeroOption {

    @Id
    private int id;
    @ManyToOne
    private THero hero;

    private String optionKey;
    private String optionValue;

}
