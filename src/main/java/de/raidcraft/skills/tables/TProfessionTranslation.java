package de.raidcraft.skills.tables;

import de.raidcraft.api.ebean.Model;
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

    public static Finder<Integer, TProfessionTranslation> find = new Finder<>(Integer.class, TProfessionTranslation.class, SkillsPlugin.class);
}
