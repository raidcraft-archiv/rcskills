package de.raidcraft.skills.tables;

import com.avaje.ebean.validation.Length;
import de.raidcraft.api.ebean.Model;
import de.raidcraft.skills.SkillsPlugin;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Represents a Ebean-mapped language object model.
 */
@Getter
@Setter
@Entity
@Table(name = "rc_language")
public class TLanguage extends Model {

    /**
     * ISO 639-1 language code in the format [language_territory]
     *
     * @see <a href="https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes">List of ISO 639-1 codes</a>
     */
    @Id
    @Column(columnDefinition = "CHAR", unique = true, length = 5)
    @Length(min = 5)
    private String code;

    /**
     * A name for the language that is appropriate for display to the user.
     */
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false, length = 32)
    private String name;

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
        result.append(" code: ").append(this.code).append(newLine);
        result.append(" name: ").append(this.name).append(newLine);
        result.append("}");

        return result.toString();
    }

    public static Finder<String, TLanguage> find = new TLanguage.Finder<String, TLanguage>(String.class, TLanguage.class, SkillsPlugin.class);
}
