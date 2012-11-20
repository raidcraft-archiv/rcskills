package de.raidcraft.skills.api.profession;

import de.raidcraft.skills.api.LevelObject;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.Skill;

import java.util.Collection;

/**
 * @author Silthus
 */
public interface PlayerProfession extends Profession, LevelObject<PlayerProfession> {

    public Hero getHero();

    public boolean isActive();

    public boolean isMastered();

    public Collection<Skill> getGainedSkills();
}
