package de.raidcraft.skills.api.skill;

import de.raidcraft.skills.api.level.AbstractLevel;
import de.raidcraft.skills.api.persistance.LevelData;

/**
 * @author Silthus
 */
public class SkillLevel extends AbstractLevel<LevelableSkill> {

    protected SkillLevel(LevelableSkill levelObject, LevelData data) {

        super(levelObject, data);
    }
}
