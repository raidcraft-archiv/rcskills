package de.raidcraft.skills.tables;

import com.avaje.ebean.validation.NotNull;
import de.raidcraft.api.database.Bean;
import de.raidcraft.skills.api.persistance.LevelData;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * @author Silthus
 */
@Entity
@Table(name = "skills_hero_professions")
public class THeroProfession implements Bean, LevelData {

    @Id
    private int id;

    @NotNull
    private String name;

    @ManyToOne
    private THero hero;

    private int level;
    private int exp;
    private boolean active;

    @OneToMany
    @JoinColumn(name = "profession_id")
    private List<THeroSkill> skills;

    @OneToMany
    @JoinColumn(name = "profession_id")
    private List<THeroResource> resources;

    @Override
    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public THero getHero() {

        return hero;
    }

    public void setHero(THero heroTable) {

        this.hero = heroTable;
    }

    public int getLevel() {

        return level;
    }

    public void setLevel(int level) {

        this.level = level;
    }

    public int getExp() {

        return exp;
    }

    public void setExp(int exp) {

        this.exp = exp;
    }

    public boolean isActive() {

        return active;
    }

    public void setActive(boolean active) {

        this.active = active;
    }

    public List<THeroSkill> getSkills() {

        return skills;
    }

    public void setSkills(List<THeroSkill> skills) {

        this.skills = skills;
    }

    public List<THeroResource> getResources() {

        return resources;
    }

    public void setResources(List<THeroResource> resources) {

        this.resources = resources;
    }
}
