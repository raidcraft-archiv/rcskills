package de.raidcraft.skills.api.persistance;

import de.raidcraft.skills.api.Obtainable;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;

import java.util.Collection;

/**
* @author Silthus
*/
public abstract class SkillData {

    protected int id;
    protected String name;
    protected String description;
    protected String[] usage;
    protected Obtainable.Type type;
    protected double cost;
    protected int neededLevel;
    protected Collection<Profession> professions;
    protected boolean allProfessionsRequired;
    protected Collection<Skill> strongParents;
    protected Collection<Skill> weakParents;

    public int getId() {

        return id;
    }

    public String getName() {

        return name;
    }

    public String getDescription() {

        return description;
    }

    public String[] getUsage() {

        return usage;
    }

    public Obtainable.Type getType() {

        return type;
    }

    public double getCost() {

        return cost;
    }

    public int getNeededLevel() {

        return neededLevel;
    }

    public Collection<Profession> getProfessions() {

        return professions;
    }

    public boolean isAllProfessionsRequired() {

        return allProfessionsRequired;
    }

    public Collection<Skill> getStrongParents() {

        return strongParents;
    }

    public Collection<Skill> getWeakParents() {

        return weakParents;
    }
}
