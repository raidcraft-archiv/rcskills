package de.raidcraft.skills.api.profession;

import de.raidcraft.skills.api.level.LevelObject;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.Skill;

import java.util.Collection;

/**
 * @author Silthus
 */
public interface LevelableProfession extends Profession, LevelObject<LevelableProfession> {

    public Hero getHero();

    public boolean isActive();

    public boolean isMastered();

    public Collection<Skill> getGainedSkills();
}
