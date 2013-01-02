package de.raidcraft.skills.api.profession;

import de.raidcraft.api.database.Database;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.Level;
import de.raidcraft.skills.api.persistance.ProfessionProperties;
import de.raidcraft.skills.api.requirement.Requirement;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.tables.THeroProfession;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
public abstract class AbstractProfession implements Profession {

    private final ProfessionProperties properties;
    private final Hero hero;
    // list of requirements to unlock this profession
    private final List<Requirement> requirements = new ArrayList<>();
    protected final THeroProfession database;
    protected final List<Skill> skills = new ArrayList<>();
    private Level<Profession> level;

    protected AbstractProfession(Hero hero, ProfessionProperties data, THeroProfession database) {

        this.properties = data;
        this.hero = hero;
        this.database = database;
        data.loadRequirements(this);
    }

    public THeroProfession getDatabase() {

        return database;
    }

    @Override
    public final Level<Profession> getLevel() {

        return level;
    }

    @Override
    public final void attachLevel(Level<Profession> level) {

        this.level = level;
    }

    @Override
    public int getId() {

        return database.getId();
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

        if (skills.size() < 1) {
            this.skills.addAll(properties.loadSkills(this));
        }
        return skills;
    }

    @Override
    public List<Skill> getUnlockedSkills() {

        List<Skill> skills = new ArrayList<>();
        for (Skill skill : this.skills) {
            if (skill.isUnlocked()) {
                skills.add(skill);
            }
        }
        return skills;
    }

    @Override
    public void addSkill(Skill skill) {

        throw new UnsupportedOperationException();
    }

    @Override
    public void removeSkill(Skill skill) {

        throw new UnsupportedOperationException();
    }

    @Override
    public List<Requirement> getRequirements() {

        return requirements;
    }

    @Override
    public void addRequirement(Requirement requirement) {

        requirements.add(requirement);
    }

    @Override
    public boolean isUnlockable() {

        for (Requirement requirement : requirements) {
            if (!requirement.isMet(getHero())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getUnlockReason() {

        for (Requirement requirement : requirements) {
            if (!requirement.isMet(getHero())) {
                return requirement.getReason(getHero());
            }
        }
        return "Beruf/Klasse kann freigeschaltet werden.";
    }

    @Override
    public void save() {

        saveLevelProgress(getLevel());
        database.setActive(isActive());
        database.setMastered(isMastered());
        Database.save(database);
    }

    @Override
    public void saveLevelProgress(Level<Profession> level) {

        database.setLevel(level.getLevel());
        database.setExp(level.getExp());
        Database.save(database);
    }

    @Override
    public String toString() {

        return getProperties().getFriendlyName();
    }

    @Override
    public int compareTo(Profession o) {

        return o.getProperties().getFriendlyName().compareTo(getProperties().getFriendlyName());
    }

    @Override
    public boolean equals(Object obj) {

        return obj instanceof Profession
                && ((Profession) obj).getId() != 0 && getId() != 0
                && ((Profession) obj).getId() == getId();
    }
}
