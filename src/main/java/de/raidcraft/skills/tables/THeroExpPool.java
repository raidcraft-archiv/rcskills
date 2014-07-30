package de.raidcraft.skills.tables;

import de.raidcraft.skills.api.persistance.LevelData;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Silthus
 */
@Entity
@Table(name = "skills_exp_pool")
@Getter
@Setter
public class THeroExpPool implements LevelData {

    @Id
    private int id;

    private int heroId;
    private String player;
    private int exp;
    private THeroProfession linkedProfession;

    @Override
    public int getLevel() {

        return 1;
    }

    @Override
    public int getExp() {

        return exp;
    }
}
