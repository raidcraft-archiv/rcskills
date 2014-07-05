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
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * Represents a Ebean-mapped skill translation object model.
 */
@Getter
@Setter
@Entity
@Table(name = "skills_skill_translation")
public class TSkillTranslation extends Model {

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

    public static Finder<Integer, TSkillTranslation> find = new Finder<>(Integer.class, TSkillTranslation.class, SkillsPlugin.class);
}
