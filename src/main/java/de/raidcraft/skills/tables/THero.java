package de.raidcraft.skills.tables;

import com.avaje.ebean.validation.NotNull;
import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.persistance.HeroData;
import de.raidcraft.skills.api.persistance.LevelData;
import de.raidcraft.skills.util.CollectionUtils;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Silthus
 */
@Entity
@Table(name = "skills_heroes")
@Getter
@Setter
public class THero implements LevelData, HeroData {

    @Id
    private int id;
    private String player;
    @NotNull
    @Column(unique = true)
    private UUID playerId;

    private String selectedProfession;
    private int exp;
    private int level;
    private double health;

    @OneToOne
    private THeroExpPool expPool;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "hero_id")
    private List<THeroProfession> professions;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "hero_id")
    private List<THeroSkill> skills;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "hero_id")
    private List<THeroOption> options;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "hero_id")
    private List<THeroAttribute> attributes;

    @Override
    public int getId() {

        return id;
    }

    @Override
    public UUID getPlayerId() {

        return this.playerId;
    }

    @Override
    public double getHealth() {

        return health;
    }

    @Override
    public int getMaxLevel() {

        return RaidCraft.getComponent(SkillsPlugin.class).getCommonConfig().hero_max_level;
    }

    @Override
    public List<String> getProfessionNames() {

        ArrayList<String> strings = new ArrayList<>();
        if (!CollectionUtils.isEmpty(getProfessions())) {
            for (THeroProfession profession : getProfessions()) {
                strings.add(profession.getName());
            }
        }
        return strings;
    }

    @Override
    public LevelData getLevelData() {

        return this;
    }

    @Override
    public int getLevel() {

        return level;
    }

    @Override
    public int getExp() {

        return exp;
    }
}
