package de.raidcraft.skills.tables;

import com.avaje.ebean.annotation.CreatedTimestamp;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Represents a Ebean-mapped skill object model.
 */
@Getter
@Setter
@Entity
@Table(name = "skills_skill")
public class TSkill {

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
    private Material iconMaterial = Material.ENCHANTED_BOOK;

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
    private int cooldown;

    /**
     * The maximum distance of the skill to have an effect.
     */
    private int reach = 1;

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
        result.append(" profession: ").append(this.profession).append(newLine);
        result.append(" creation timestamp: ").append(this.cretimestamp).append(newLine);
        result.append(" update timestamp: ").append(this.updtimestamp).append(newLine);
        result.append(" enabled: ").append(this.enabled).append(newLine);
        result.append(" hidden: ").append(this.hidden).append(newLine);
        result.append(" icon material: ").append(this.iconMaterial).append(newLine);
        result.append(" required level: ").append(this.reqLevel).append(newLine);
        result.append(" maximum level: ").append(this.maxLevel).append(newLine);
        result.append(" cooldown time: ").append(this.cooldown).append(newLine);
        result.append(" reach: ").append(this.reach).append(newLine);
        result.append("}");

        return result.toString();
    }
}
