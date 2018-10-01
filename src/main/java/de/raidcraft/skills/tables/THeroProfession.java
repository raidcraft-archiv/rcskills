package de.raidcraft.skills.tables;

import de.raidcraft.skills.api.persistance.LevelData;
import io.ebean.annotation.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

/**
 * @author Silthus
 */
@Entity
@Table(name = "rc_skills_hero_professions")
@Getter
@Setter
public class THeroProfession implements LevelData {

    @Id
    private int id;

    @NotNull
    private String name;

    @ManyToOne
    private THero hero;

    private int level;
    private int exp;
    private boolean active;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "profession_id")
    private List<THeroSkill> skills;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "profession_id")
    private List<THeroResource> resources;

    @Override
    public int getLevel() {

        return level;
    }

    @Override
    public int getExp() {

        return exp;
    }
}
