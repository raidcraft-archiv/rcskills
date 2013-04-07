package de.raidcraft.skills.tables;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.database.Bean;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.persistance.LevelData;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Silthus
 */
@Entity
@Table(name = "skills_exp_pool")
public class THeroExpPool implements LevelData, Bean {

    @Id
    private int id;
    private int heroId;
    private String player;
    private int exp;
    private THeroProfession linkedProfession;

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public int getHeroId() {

        return heroId;
    }

    public void setHeroId(int heroId) {

        this.heroId = heroId;
    }

    public String getPlayer() {

        return player;
    }

    public void setPlayer(String player) {

        this.player = player;
    }

    @Override
    public int getLevel() {

        return 1;
    }

    public int getExp() {

        return exp;
    }

    public void setExp(int exp) {

        this.exp = exp;
    }

    public THeroProfession getLinkedProfession() {

        return linkedProfession;
    }

    public void setLinkedProfession(THeroProfession linkedProfession) {

        this.linkedProfession = linkedProfession;
    }

    public void delete() {

        RaidCraft.getDatabase(SkillsPlugin.class).delete(this);
    }
}
