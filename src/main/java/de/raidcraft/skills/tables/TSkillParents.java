package de.raidcraft.skills.tables;

import de.raidcraft.skills.api.ParentType;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Silthus
 */
@Entity
@Table(name = "s_skill_parents")
public class TSkillParents {

    @Id
    private int id;

    @ManyToOne
    private TSkill child;

    private TSkill parent;

    private ParentType type;

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public TSkill getChild() {

        return child;
    }

    public void setChild(TSkill child) {

        this.child = child;
    }

    public TSkill getParent() {

        return parent;
    }

    public void setParent(TSkill parent) {

        this.parent = parent;
    }

    public ParentType getType() {

        return type;
    }

    public void setType(ParentType type) {

        this.type = type;
    }
}
