package de.raidcraft.skills.api.profession;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.Level;
import de.raidcraft.skills.api.persistance.ProfessionData;
import de.raidcraft.skills.api.skill.Skill;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Silthus
 */
public abstract class AbstractProfession implements Profession {

    private final int id;
    private final Hero hero;
    private final String name;
    private final String friendlyName;
    private final String description;
    private boolean active;
    private boolean mastered;
    // maps skills with the minimal required level
    private final Set<Skill> skills;
    private final Set<Skill> unlockedSkills = new HashSet<>();
    // parent child collections
    private final Collection<Profession> strongParents = new LinkedHashSet<>();
    private final Collection<Profession> weakParents = new LinkedHashSet<>();
    // the level object holding our level and stuff
    private Level<Profession> level;

    protected AbstractProfession(Hero hero, ProfessionData data) {

        this.id = data.getId();
        this.hero = hero;
        this.name = data.getName();
        this.friendlyName = data.getFriendlyName();
        this.description = data.getDescription();
        this.skills = data.getSkills();
        this.active = data.isActive();
        this.mastered = data.isMastered();
        loadSkills();
        attachLevel(new ProfessionLevel(this, data));
    }

    private void loadSkills() {

        for (Skill skill : skills) {
            if (skill.isActive() && skill.isUnlocked()) {
                unlockedSkills.add(skill);
            }
        }
    }

    @Override
    public int getId() {

        return id;
    }

    @Override
    public Hero getHero() {

        return hero;
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
    public boolean isActive() {

        return active;
    }

    @Override
    public boolean isMastered() {

        return mastered;
    }

    @Override
    public Set<Skill> getSkills() {

        return skills;
    }

    @Override
    public Set<Skill> getUnlockedSkills() {

        return unlockedSkills;
    }

    @Override
    public Level<Profession> getLevel() {

        return level;
    }

    @Override
    public void attachLevel(Level<Profession> level) {

        this.level = level;
    }

    /*//////////////////////////////////////////////////////
    // Parent/Child Relationship Methods beyond this line
    /////////////////////////////////////////////////////*/

    @Override
    public Collection<Profession> getStrongParents() {

        return strongParents;
    }

    @Override
    public Collection<Profession> getWeakParents() {

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

        return "[P-" + getClass().getName() + "]" + getName();
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Profession) {
            return ((Profession) obj).getName().equalsIgnoreCase(getName())
                    && getHero().equals(((Profession) obj).getHero());
        }
        return false;
    }
}
