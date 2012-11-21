package de.raidcraft.skills.api.profession;

import de.raidcraft.skills.api.level.Level;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.ProfessionData;
import de.raidcraft.skills.api.skill.Skill;

import java.util.Collection;

/**
 * @author Silthus
 */
public abstract class AbstractLevelableProfession extends AbstractProfession implements LevelableProfession {

    private final Hero hero;
    private Level<LevelableProfession> level;
    private boolean active;
    private boolean mastered;
    private boolean selected;
    private Collection<Skill> gainedSkills;

    protected AbstractLevelableProfession(Hero hero, ProfessionData data) {

        super(data);
        this.hero = hero;
    }

    @Override
    public void attachLevel(Level<LevelableProfession> level) {

        this.level = level;
    }

    @Override
    public Level<LevelableProfession> getLevel() {

        return level;
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
    public boolean isSelected() {

        return selected;
    }

    @Override
    public Collection<Skill> getGainedSkills() {

        return gainedSkills;
    }
}
