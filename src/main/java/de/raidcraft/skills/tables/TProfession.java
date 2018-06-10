package de.raidcraft.skills.tables;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Represents a Ebean-mapped profession object model.
 */
@Getter
@Setter
@Entity
@Table(name = "skills_profession")
public class TProfession {

    /**
     * A unique key.
     */
    @Id
    @Column(unique = true, nullable = false, length = 32)
    private String nameKey;

    /**
     * The parent of this profession.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private TProfession parent;

    /**
     * When the profession is max out.
     */
    private int maxLevel = 1;

    /**
     * "Beruf" oder "Profession"
     */
    private String type;

    /**
     * A string representation of this object.
     *
     * Only for debugging.
     */
    @Override
    public String toString() {

        final StringBuilder result = new StringBuilder();
        final String newLine = System.getProperty("line.separator");

        result.append(this.getClass().getName()).append(" Object {").append(newLine);
        result.append(" key: ").append(this.nameKey).append(newLine);
        result.append(" parent: ").append(this.parent).append(newLine);
        result.append(" maxLevel: ").append(this.maxLevel).append(newLine);
        result.append("}");

        return result.toString();
    }
}
