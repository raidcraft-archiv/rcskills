package de.raidcraft.skills.api.profession;

import de.raidcraft.skills.api.Level;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.ProfessionData;
import de.raidcraft.skills.api.skill.Skill;

import java.util.Collection;

/**
 * @author Silthus
 */
public abstract class AbstractPlayerProfession extends AbstractProfession implements PlayerProfession {

    private final Hero hero;
    private Level<PlayerProfession> level;
    private boolean active;
    private boolean mastered;
    private Collection<Skill> gainedSkills;

    protected AbstractPlayerProfession(Hero hero, ProfessionData data) {

        super(data);
        this.hero = hero;
    }

    @Override
    public void attachLevel(Level<PlayerProfession> level) {

        this.level = level;
    }

    @Override
    public Level<PlayerProfession> getLevel() {

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
    public Collection<Skill> getGainedSkills() {

        return gainedSkills;
    }
}
