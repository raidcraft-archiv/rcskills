package de.raidcraft.skills.tables;

import javax.persistence.*;
import java.util.List;

/**
 * @author Silthus
 */
@Entity
@Table(name = "s_player_profession")
public class TPlayerProfession {

    @Id
    private int id;

    @ManyToOne
    private TPlayer player;

    @ManyToOne
    private TProfession profession;

    @OneToMany(cascade = CascadeType.ALL)
    private List<TPlayerProfessionSkill> skills;

    private boolean active;

    private boolean mastered;

    private boolean selected;

    private int exp;

    private int currentLevel;

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public TPlayer getPlayer() {

        return player;
    }

    public void setPlayer(TPlayer player) {

        this.player = player;
    }

    public TProfession getProfession() {

        return profession;
    }

    public void setProfession(TProfession profession) {

        this.profession = profession;
    }

    public List<TPlayerProfessionSkill> getSkills() {

        return skills;
    }

    public void setSkills(List<TPlayerProfessionSkill> skills) {

        this.skills = skills;
    }

    public boolean isActive() {

        return active;
    }

    public void setActive(boolean active) {

        this.active = active;
    }

    public boolean isMastered() {

        return mastered;
    }

    public void setMastered(boolean mastered) {

        this.mastered = mastered;
    }

    public boolean isSelected() {

        return selected;
    }

    public void setSelected(boolean selected) {

        this.selected = selected;
    }

    public int getExp() {

        return exp;
    }

    public void setExp(int exp) {

        this.exp = exp;
    }

    public int getCurrentLevel() {

        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {

        this.currentLevel = currentLevel;
    }
}
