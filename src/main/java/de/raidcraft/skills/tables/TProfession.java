package de.raidcraft.skills.tables;

import de.raidcraft.api.ebean.Model;
import de.raidcraft.skills.SkillsPlugin;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Represents a Ebean-mapped profession object model.
 */
@Getter
@Setter
@Entity
@Table(name = "skills_profession")
public class TProfession extends Model {

    /**
     * A unique key.
     */
    @Id
    @Column(unique = true, nullable = false, length = 32)
    private String nameKey;

    public static Finder<String, TProfession> find = new Finder<>(String.class, TProfession.class, SkillsPlugin.class);
}
