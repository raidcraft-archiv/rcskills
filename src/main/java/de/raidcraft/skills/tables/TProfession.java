package de.raidcraft.skills.tables;

import com.avaje.ebean.validation.NotNull;

import javax.persistence.*;
import java.util.List;

/**
 * @author Silthus
 */
@Entity
@Table(name = "s_professions")
public class TProfession {

    @Id
    private int id;

    @OneToMany
    private List<TProfessionSkill> skills;

    @OneToMany
    private List<TPlayerProfession> playerProfessions;

    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL)
    private List<TProfessionParents> parents;

    @NotNull
    @Column(unique = true)
    private String name;

    @Column(name = "friendly_name")
    private String friendlyName;

    private String description;

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public List<TProfessionSkill> getSkills() {

        return skills;
    }

    public void setSkills(List<TProfessionSkill> skills) {

        this.skills = skills;
    }

    public List<TPlayerProfession> getPlayerProfessions() {

        return playerProfessions;
    }

    public void setPlayerProfessions(List<TPlayerProfession> playerProfessions) {

        this.playerProfessions = playerProfessions;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getFriendlyName() {

        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {

        this.friendlyName = friendlyName;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public List<TProfessionParents> getParents() {

        return parents;
    }

    public void setParents(List<TProfessionParents> parents) {

        this.parents = parents;
    }
}
