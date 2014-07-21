package de.raidcraft.skills.tables;

import com.avaje.ebean.validation.Length;
import de.raidcraft.api.ebean.Model;
import de.raidcraft.api.language.table.TLanguage;
import de.raidcraft.skills.SkillsPlugin;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * Represents a Ebean-mapped profession translation object model.
 */
@Getter
@Setter
@Entity
@Table(name = "skills_profession_translation")
public class TProfessionTranslation extends Model {

    /**
     * A unique Id.
     */
    @Id
    private long id;

    /**
     * The profession object to be translated.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @PrimaryKeyJoinColumn
    private TProfession profession;

    /**
     * The language code of the translation.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @PrimaryKeyJoinColumn
    private TLanguage language;

    /**
     * The name of the profession.
     */
    @Column(length = 32)
    private String name;

    /**
     * The description of the profession.
     */
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String description;

    /**
     * The profession tag.
     */
    @Column(columnDefinition = "CHAR", length = 3)
    @Length(min = 3)
    private String tag;

    /**
     * A string representation of this object.
     * </p>
     * Only for debugging.
     */
    @Override
    public String toString() {

        final StringBuilder result = new StringBuilder();
        final String newLine = System.getProperty("line.separator");

        result.append(this.getClass().getName()).append(" Object {").append(newLine);
        result.append(" id: ").append(this.id).append(newLine);
        result.append(" profession: ").append(this.profession).append(newLine);
        result.append(" language: ").append(this.language).append(newLine);
        result.append(" name: ").append(this.name).append(newLine);
        result.append(" description: ").append(this.description).append(newLine);
        result.append(" tag: ").append(this.tag).append(newLine);
        result.append("}");

        return result.toString();
    }

    public static Finder<Integer, TProfessionTranslation> find = new Finder<>(Integer.class, TProfessionTranslation.class, SkillsPlugin.class);
}
