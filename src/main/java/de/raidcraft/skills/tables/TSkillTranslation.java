package de.raidcraft.skills.tables;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Represents a Ebean-mapped skill translation object model.
 */
@Getter
@Setter
@Entity
@Table(name = "skills_skill_translation")
public class TSkillTranslation {

    /**
     * A unique Id.
     */
    @Id
    private long id;

    /**
     * The skill object to be translated.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private TSkill skill;

    /**
     * The language code of the translation.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private TLanguage language;

    /**
     * The name of the skill.
     */
    @Column(length = 32)
    private String name;

    /**
     * The description of the skill.
     */
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String description;

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
        result.append(" id: ").append(this.id).append(newLine);
        result.append(" skill: ").append(this.skill).append(newLine);
        result.append(" language: ").append(this.language).append(newLine);
        result.append(" name: ").append(this.name).append(newLine);
        result.append(" description: ").append(this.description).append(newLine);
        result.append("}");

        return result.toString();
    }
}
