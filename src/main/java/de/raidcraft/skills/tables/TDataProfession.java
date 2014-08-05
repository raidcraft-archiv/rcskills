package de.raidcraft.skills.tables;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Dragonfire
 */
@Getter
@Setter
@Entity
@Table(name = "skills_data_profession")
public class TDataProfession {

    @Id
    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;
    private int maxLevel;
    private String formula;
    private String parent;
    @Column(columnDefinition = "TEXT")
    private String skills;
    private String type;
}
