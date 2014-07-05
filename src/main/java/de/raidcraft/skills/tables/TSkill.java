package de.raidcraft.skills.tables;

import com.avaje.ebean.annotation.CreatedTimestamp;
import de.raidcraft.api.ebean.Model;
import de.raidcraft.skills.SkillsPlugin;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * Represents a Ebean-mapped skill object model.
 */
@Getter
@Setter
@Entity
@Table(name = "skills_skill")
public class TSkill extends Model {

    /**
     * A unique key.
     */
    @Id
    @Column(unique = true, nullable = false, length = 32)
    private String nameKey;

    /**
     * The associated profession.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @PrimaryKeyJoinColumn
    private TProfession profession;

    /**
     * A timestamp that is set to the datetime when the skill is created/inserted.
     */
    @Setter(AccessLevel.PROTECTED)
    @CreatedTimestamp
    private Timestamp cretimestamp;

    /**
     * The last modified timestamp of the skill.
     */
    private Timestamp updtimestamp;

    /**
     * Whether the skill is enabled/disabled.
     */
    private boolean enabled = false;

    /**
     * Whether the skill is hidden/visible.
     */
    private boolean hidden = true;

    /**
     * When the skill mastery is reached.
     */
    private int maxLevel = 1;

    /**
     * The maximum distance of the skill to have an effect.
     */
    private int range = 1;

    public static Finder<String, TSkill> find = new TSkill.Finder<>(String.class, TSkill.class, SkillsPlugin.class);
}
