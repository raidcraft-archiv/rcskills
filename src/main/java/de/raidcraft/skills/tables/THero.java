package de.raidcraft.skills.tables;

import com.avaje.ebean.validation.NotNull;
import de.raidcraft.skills.api.persistance.LevelData;

import javax.persistence.*;
import java.util.List;

/**
 * @author Silthus
 */
@Entity
@Table(name = "skills_hero")
public class THero implements LevelData {

    @Id
    private int id;
    @NotNull
    @Column(unique = true)
    private String player;
    private int exp;
    private int level;
    private String selectedProfession;
    @OneToMany(cascade = CascadeType.ALL)
    private List<THeroProfession> professions;
    @OneToMany(cascade = CascadeType.ALL)
    private List<THeroSkill> skills;

    public int getId() {

        return id;
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

    @Override
    public int getMaxLevel() {

        throw new UnsupportedOperationException();
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

    public String getSelectedProfession() {

        return selectedProfession;
    }

    public void setSelectedProfession(String selectedProfession) {

        this.selectedProfession = selectedProfession;
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
}
