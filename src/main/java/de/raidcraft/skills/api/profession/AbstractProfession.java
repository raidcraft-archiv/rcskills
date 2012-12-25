package de.raidcraft.skills.api.profession;

import de.raidcraft.api.database.Database;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.Level;
import de.raidcraft.skills.api.persistance.ProfessionProperties;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.tables.THeroProfession;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Silthus
 */
public abstract class AbstractProfession implements Profession {

    private final int id;
    private final ProfessionProperties properties;
    private final Hero hero;
    private final List<Skill> unlockedSkills = new ArrayList<>();
    protected final THeroProfession database;
    private List<Skill> skills;
    // parent child collections
    private Set<Profession> strongParents = null;
    private Set<Profession> weakParents = null;

    protected AbstractProfession(Hero hero, ProfessionProperties data, THeroProfession database) {

        this.id = database.getId();
        this.properties = data;
        this.hero = hero;
        this.database = database;
    }

    @Override
    public int getId() {

        return id;
    }

    @Override
    public String getName() {

        return getProperties().getName();
    }

    @Override
    public ProfessionProperties getProperties() {

        return properties;
    }

    @Override
    public Hero getHero() {

        return hero;
    }

    @Override
    public boolean isActive() {

        return database.isActive();
    }

    @Override
    public void setActive(boolean active) {

        database.setActive(active);
    }

    @Override
    public boolean isMastered() {

        return database.isMastered();
    }

    @Override
    public int getMaxLevel() {

        return getProperties().getMaxLevel();
    }

    @Override
    public List<Skill> getSkills() {

        if (skills == null || skills.size() < 1) {
            this.skills = properties.loadSkills(getHero(), this);
            // go thru the skills and sort unlocked
            for (Skill skill : skills) {
                if (skill.isUnlocked()) {
                    unlockedSkills.add(skill);
                }
            }
        }
        return skills;
    }

    @Override
    public List<Skill> getUnlockedSkills() {

        return unlockedSkills;
    }

    @Override
    public final void saveLevelProgress(Level<Profession> level) {

        database.setLevel(level.getLevel());
        database.setExp(level.getExp());
        Database.save(database);
    }

    /*//////////////////////////////////////////////////////
    // Parent/Child Relationship Methods beyond this line
    /////////////////////////////////////////////////////*/

    @Override
    public Set<Profession> getStrongParents() {

        if (strongParents == null) {
            this.strongParents = getProperties().loadStrongParents(getHero(), this);
        }
        return this.strongParents;
    }

    @Override
    public Set<Profession> getWeakParents() {

        if (weakParents == null) {
            this.weakParents = getProperties().loadWeakParents(getHero(), this);
        }
        return this.weakParents;
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

        return "[P" + getId() + ":" + getProperties().getName() + "]";
    }

    @Override
    public int compareTo(Profession o) {

        return o.getProperties().getFriendlyName().compareTo(getProperties().getFriendlyName());
    }

    @Override
    public boolean equals(Object obj) {

        return obj instanceof Profession
                && ((Profession) obj).getProperties().getName().equalsIgnoreCase(getProperties().getName())
                && getHero().equals(((Profession) obj).getHero());
    }
}
