package de.raidcraft.skills.api.profession;

import de.raidcraft.api.inheritance.Child;
import de.raidcraft.skills.api.persistance.ProfessionData;
import de.raidcraft.skills.api.skill.Skill;

import java.util.Collection;
import java.util.Set;

/**
 * @author Silthus
 */
public abstract class AbstractProfession implements Profession, Child<Profession> {

    private final int id;
    private final String name;
    private final String friendlyName;
    private final String description;
    // maps skills with the minimal required level
    private final Set<Skill> skills;
    // parent child collections
    private final Collection<Profession> strongParents;
    private final Collection<Profession> weakParents;

    protected AbstractProfession(ProfessionData data) {

        this.id = data.getId();
        this.name = data.getName();
        this.friendlyName = data.getFriendlyName();
        this.description = data.getDescription();
        this.skills = data.getSkills();
        this.strongParents = data.getStrongParents();
        this.weakParents = data.getWeakParents();
    }

    @Override
    public int getId() {

        return id;
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public String getFriendlyName() {

        return friendlyName;
    }

    @Override
    public String getTag() {

        return friendlyName.toUpperCase().substring(0, 2).trim();
    }

    @Override
    public String getDescription() {

        return description;
    }

    @Override
    public Collection<Skill> getSkills() {

        return skills;
    }

    /*//////////////////////////////////////////////////////
    // Parent/Child Relationship Methods beyond this line
    /////////////////////////////////////////////////////*/

    @Override
    public Collection<Profession> getStrongParents() {

        return strongParents;
    }

    @Override
    public Collection<Profession> getWeaksParents() {

        return weakParents;
    }

    @Override
    public void addStrongParent(Profession profession) {

        strongParents.add(profession);
    }

    @Override
    public void addWeakParent(Profession profession) {

        weakParents.add(profession);
    }

    @Override
    public void removeStrongParent(Profession profession) {

        strongParents.remove(profession);
    }

    @Override
    public void removeWeakParent(Profession profession) {

        weakParents.remove(profession);
    }

    @Override
    public String toString() {

        return "[P" + getId() + "-" + getClass().getName() + "]" + getName();
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof AbstractProfession) {
            return ((AbstractProfession) obj).getId() == getId();
        }
        return false;
    }
}
