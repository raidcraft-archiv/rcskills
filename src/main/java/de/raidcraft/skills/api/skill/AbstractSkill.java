package de.raidcraft.skills.api.skill;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillData;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author Silthus
 */
public abstract class AbstractSkill implements Skill {

    private String name;
    private String friendlyName;
    private String description;
    private int requiredLevel;
    private String[] usage;
    private Collection<Skill> strongParents = new HashSet<>();
    private Collection<Skill> weakParents = new HashSet<>();

    protected AbstractSkill(SkillData data) {

        this.name = getClass().getAnnotation(SkillInformation.class).name();
        this.friendlyName = data.getFriendlyName();
        this.description = data.getDescription();
        this.requiredLevel = data.getRequiredLevel();
        this.usage = data.getUsage();
        load(data.getData());
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public String getFriendlyName() {

        return friendlyName;
    }

    protected void setDescription(String description) {

        this.description = description;
    }

    @Override
    public String getDescription() {

        return description;
    }

    @Override
    public String getDescription(Hero hero) {

        return getDescription();
    }

    @Override
    public int getRequiredLevel() {

        return requiredLevel;
    }

    @Override
    public String[] getUsage() {

        return usage;
    }

    @Override
    public Collection<Skill> getStrongParents() {

        return strongParents;
    }

    @Override
    public Collection<Skill> getWeakParents() {

        return weakParents;
    }

    @Override
    public void addStrongParent(Skill skill) {

        strongParents.add(skill);
    }

    @Override
    public void addWeakParent(Skill skill) {

        weakParents.add(skill);
    }

    @Override
    public void removeStrongParent(Skill skill) {

        strongParents.remove(skill);
    }

    @Override
    public void removeWeakParent(Skill skill) {

        weakParents.remove(skill);
    }

    @Override
    public String toString() {

        return "[S-" + getClass().getName() + "]" + getName();
    }

    @Override
    public boolean equals(Object obj) {

        return obj instanceof Skill && ((Skill) obj).getName().equalsIgnoreCase(getName());
    }

    @Override
    public int compareTo(Skill o) {

        if (o.getRequiredLevel() == getRequiredLevel()) return 0;
        if (getRequiredLevel() > o.getRequiredLevel()) return 1;
        return -1;
    }
}
