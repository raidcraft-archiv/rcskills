package de.raidcraft.skills.tables;

import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.task.Model;
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

    /**
     * When the profession is max out.
     */
    private int maxLevel = 1;

    public static Finder<String, TProfession> find = new Finder<>(String.class, TProfession.class, SkillsPlugin.class);
}
