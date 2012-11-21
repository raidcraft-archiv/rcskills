package de.raidcraft.skills.api.skill;

import de.raidcraft.skills.api.level.Levelable;
import de.raidcraft.skills.api.hero.Hero;

/**
 * @author Silthus
 */
public interface LevelableSkill extends Levelable<LevelableSkill>, Skill {

    public Hero getHero();
}
