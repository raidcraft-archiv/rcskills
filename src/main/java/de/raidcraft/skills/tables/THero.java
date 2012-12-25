package de.raidcraft.skills.tables;

import com.avaje.ebean.validation.NotNull;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.database.Bean;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.persistance.HeroData;
import de.raidcraft.skills.api.persistance.LevelData;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
@Entity
@Table(name = "skills_heroes")
public class THero implements LevelData, HeroData, Bean {

    @Id
    private int id;

    @NotNull
    @Column(unique = true)
    private String player;
    private int exp;
    private int level;
    private int health;

    @OneToMany
    @JoinColumn(name = "hero_id")
    private List<THeroProfession> professions;

    @OneToMany
    @JoinColumn(name = "hero_id")
    private List<THeroSkill> skills;

    @Override
    public List<String> getProfessionNames() {

        ArrayList<String> strings = new ArrayList<>();
        if (getProfessions() != null) {
            for (THeroProfession profession : getProfessions()) {
                strings.add(profession.getName());
            }
        }
        return strings;
    }

    @Override
    public List<String> getSkillNames() {

        ArrayList<String> strings = new ArrayList<>();
        if (getSkills() != null) {
            for (THeroSkill skill : getSkills()) {
                strings.add(skill.getName());
            }
        }
        return strings;
    }

    public int getId() {

        return id;
    }

    @Override
    public String getName() {

        return player;
    }

    @Override
    public int getMaxLevel() {

        return RaidCraft.getComponent(SkillsPlugin.class).getCommonConfig().hero_max_level;
    }

    public void setId(int id) {

        this.id = id;
    }

    public String getPlayer() {

        return player;
    }

    public void setPlayer(String player) {

        this.player = player;
    }

    public int getExp() {

        return exp;
    }

    public void setExp(int exp) {

        this.exp = exp;
    }

    public int getLevel() {

        return level;
    }

    public void setLevel(int level) {

        this.level = level;
    }

    @Override
    public LevelData getLevelData() {

        return this;
    }

    public List<THeroProfession> getProfessions() {

        return professions;
    }

    public void setProfessions(List<THeroProfession> professions) {

        this.professions = professions;
    }

    public List<THeroSkill> getSkills() {

        return skills;
    }

    public void setSkills(List<THeroSkill> skills) {

        this.skills = skills;
    }

    @Override
    public int getHealth() {

        return health;
    }

    public void setHealth(int health) {

        this.health = health;
    }
}
