package de.raidcraft.skills.api.level;

import de.raidcraft.skills.api.persistance.LevelData;
import de.raidcraft.skills.api.skill.LevelableSkill;

/**
 * @author Silthus
 */
public abstract class SkillLevel extends AbstractLevel<LevelableSkill> {

    public SkillLevel(LevelableSkill levelObject, LevelData data) {

        super(levelObject, data);
    }

    public SkillLevel(LevelableSkill levelObject) {

        super(levelObject);
    }

    @Override
    public void addExp(int exp) {

        super.addExp(exp);
        // lets add some exp to the profession of the skill
        // TODO: maybe change the factor by a config value
        getLevelObject().getProfession().getLevel().addExp(exp);
    }

    @Override
    public void removeExp(int exp) {

        super.removeExp(exp);
        // lets remove the same amount of exp from the profession
        getLevelObject().getLevel().removeExp(exp);
    }
}
