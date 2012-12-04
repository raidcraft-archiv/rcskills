package de.raidcraft.skills.api.profession;

import com.avaje.ebean.Ebean;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.Level;
import de.raidcraft.skills.api.persistance.ProfessionProperties;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.tables.THeroProfession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * @author Silthus
 */
public abstract class AbstractProfession implements Profession {

    private final int id;
    private final ProfessionProperties properties;
    private final Hero hero;
    private final List<Skill> unlockedSkills = new ArrayList<>();
    private List<Skill> skills;
    // parent child collections
    private final Collection<Profession> strongParents = new LinkedHashSet<>();
    private final Collection<Profession> weakParents = new LinkedHashSet<>();
    private boolean active;
    private boolean mastered;
    // the level object holding our level and stuff
    private Level<Profession> level;

    protected AbstractProfession(Hero hero, ProfessionProperties data, THeroProfession database) {

        this.id = database.getId();
        this.properties = data;
        this.hero = hero;
        this.active = database.isActive();
        this.mastered = database.isMastered();
    }

    @Override
    public int getId() {

        return id;
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

        return active;
    }

    @Override
    public boolean isMastered() {

        return mastered;
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
    public Level<Profession> getLevel() {

        return level;
    }

    @Override
    public void attachLevel(Level<Profession> level) {

        this.level = level;
    }

    @Override
    public void onLevelUp(Level<Profession> level) {
        // override if needed
    }

    @Override
    public void onLevelDown(Level<Profession> level) {
        // override if needed
    }

    @Override
    public final void saveLevelProgress(Level<Profession> level) {

        THeroProfession profession = Ebean.find(THeroProfession.class, getProperties());
        profession.setLevel(level.getLevel());
        profession.setExp(level.getExp());
        Ebean.save(profession);
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

        return "[P" + getId() + "-" + getClass().getName() + "]" + getProperties().getName();
    }

    @Override
    public int compareTo(Profession o) {

        return o.getProperties().getFriendlyName().compareTo(getProperties().getFriendlyName());
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Profession) {
            return ((Profession) obj).getProperties().getName().equalsIgnoreCase(getProperties().getName())
                    && getHero().equals(((Profession) obj).getHero());
        }
        return false;
    }
}
