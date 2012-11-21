package de.raidcraft.skills.api.profession;

import de.raidcraft.skills.api.level.Levelable;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.Skill;

import java.util.Collection;

/**
 * @author Silthus
 */
public interface LevelableProfession extends Profession, Levelable<LevelableProfession> {

    public Hero getHero();

    public boolean isActive();

    public boolean isMastered();

    public boolean isSelected();

    public Collection<Skill> getGainedSkills();
}
