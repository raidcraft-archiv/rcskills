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
@Table(name = "skills_data_alias")
public class TDataAlias {

    @Id
    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String parent;
    private String skill;
    private boolean hidden = false;
}
