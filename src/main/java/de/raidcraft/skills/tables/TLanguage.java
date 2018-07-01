package de.raidcraft.skills.tables;

import com.avaje.ebean.validation.Length;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Represents a Ebean-mapped language object model.
 */
@Getter
@Setter
@Entity
@Table(name = "rc_language")
public class TLanguage {

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
     * A displayName for the language that is appropriate for display to the user.
     */
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false, length = 32)
    private String name;

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
        result.append(" code: ").append(this.code).append(newLine);
        result.append(" displayName: ").append(this.name).append(newLine);
        result.append("}");

        return result.toString();
    }
}
