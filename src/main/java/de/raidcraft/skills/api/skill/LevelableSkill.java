package de.raidcraft.skills.api.skill;

import de.raidcraft.skills.api.level.Levelable;

/**
 * @author Silthus
 */
@IgnoredSkill
public interface LevelableSkill extends Levelable<LevelableSkill>, Skill {

    public boolean isMastered();
}
