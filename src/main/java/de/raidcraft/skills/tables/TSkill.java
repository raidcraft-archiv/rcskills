package de.raidcraft.skills.tables;

import com.avaje.ebean.annotation.CreatedTimestamp;
import de.raidcraft.api.ebean.Model;
import de.raidcraft.skills.SkillsPlugin;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
     * The icon material the skill is using.
     */
    @Enumerated(EnumType.STRING)
    private Material iconMaterial;

    /**
     * The character level requirement.
     */
    private int reqLevel = 1;

    /**
     * When the skill mastery is reached.
     */
    private int maxLevel = 1;

    /**
     * The length of the cooldown
     */
    private int cooldown = 0;

    /**
     * The maximum distance of the skill to have an effect.
     */
    private int range = 1;

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
        result.append(" Key: ").append(this.getNameKey()).append(newLine);
        result.append(" Profession: ").append(this.getProfession()).append(newLine);
        result.append(" Creation timestamp: ").append(this.getCretimestamp()).append(newLine);
        result.append(" Update timestamp: ").append(this.getUpdtimestamp()).append(newLine);
        result.append(" Enabled: ").append(this.isEnabled()).append(newLine);
        result.append(" Hidden: ").append(this.isHidden()).append(newLine);
        result.append(" Icon material: ").append(this.getIconMaterial()).append(newLine);
        result.append(" Required level: ").append(this.getReqLevel()).append(newLine);
        result.append(" Maximum level: ").append(this.getMaxLevel()).append(newLine);
        result.append(" Cooldown time: ").append(this.getCooldown()).append(newLine);
        result.append(" Range: ").append(this.getRange()).append(newLine);
        result.append("}");

        return result.toString();
    }

    public static Finder<String, TSkill> find = new TSkill.Finder<>(String.class, TSkill.class, SkillsPlugin.class);
}
