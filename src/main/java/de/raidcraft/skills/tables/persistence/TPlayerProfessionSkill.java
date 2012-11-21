package de.raidcraft.skills.tables.persistence;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Silthus
 */
@Entity
@Table(name = "s_player_profession_skill")
public class TPlayerProfessionSkill {

    @Id
    private int id;

    @ManyToOne
    private TPlayerProfession playerProfession;

    @ManyToOne
    private TProfessionSkill professionSkill;

    private int currentLevel;

    private int exp;

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public TPlayerProfession getPlayerProfession() {

        return playerProfession;
    }

    public void setPlayerProfession(TPlayerProfession playerProfession) {

        this.playerProfession = playerProfession;
    }

    public TProfessionSkill getProfessionSkill() {

        return professionSkill;
    }

    public void setProfessionSkill(TProfessionSkill professionSkill) {

        this.professionSkill = professionSkill;
    }

    public int getLevel() {

        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {

        this.currentLevel = currentLevel;
    }

    public int getExp() {

        return exp;
    }

    public void setExp(int exp) {

        this.exp = exp;
    }
}
