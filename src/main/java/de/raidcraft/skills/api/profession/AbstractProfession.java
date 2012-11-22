package de.raidcraft.skills.api.profession;

import com.avaje.ebean.Ebean;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.Level;
import de.raidcraft.skills.api.persistance.ProfessionData;
import de.raidcraft.skills.api.persistance.ProfessionProperties;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.tables.THeroProfession;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Silthus
 */
public abstract class AbstractProfession implements Profession {

    private final ProfessionProperties properties;
    private final Hero hero;
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

        this.properties = data;
        this.hero = hero;
        this.skills = data.getSkills();
        this.active = data.isActive();
        this.mastered = data.isMastered();
        loadSkills();
    }

    private void loadSkills() {

        for (Skill skill : skills) {
            if (skill.isActive() && skill.isUnlocked()) {
                unlockedSkills.add(skill);
            }
        }
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
    public String getTag() {

        return getProperties().getName().toUpperCase().substring(0, 2).trim();
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

    @Override
    public void increaseLevel(Level<Profession> level) {
        //TODO: implement
    }

    @Override
    public void decreaseLevel(Level<Profession> level) {
        //TODO: implement
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

        return "[P" + getProperties().getId() + "-" + getClass().getName() + "]" + getProperties().getName();
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
