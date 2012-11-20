package de.raidcraft.skills.api.skill;

import de.raidcraft.skills.api.level.LevelObject;
import de.raidcraft.skills.api.hero.Hero;

/**
 * @author Silthus
 */
public interface LevelableSkill extends LevelObject<LevelableSkill>, Skill {

    public Hero getHero();
}
