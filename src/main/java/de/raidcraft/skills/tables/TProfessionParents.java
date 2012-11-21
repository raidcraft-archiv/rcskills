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
@Table(name = "s_profession_parents")
public class TProfessionParents {

    @Id
    private int id;

    @ManyToOne
    private TProfession child;

    private TProfession parent;

    private ParentType type;

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public TProfession getChild() {

        return child;
    }

    public void setChild(TProfession child) {

        this.child = child;
    }

    public TProfession getParent() {

        return parent;
    }

    public void setParent(TProfession parent) {

        this.parent = parent;
    }

    public ParentType getType() {

        return type;
    }

    public void setType(ParentType type) {

        this.type = type;
    }
}
