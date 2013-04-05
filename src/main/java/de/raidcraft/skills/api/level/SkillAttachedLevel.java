package de.raidcraft.skills.api.level;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.persistance.LevelData;
import de.raidcraft.skills.api.skill.LevelableSkill;

/**
 * @author Silthus
 */
public class SkillAttachedLevel extends ConfigurableAttachedLevel<LevelableSkill> {

    public SkillAttachedLevel(LevelableSkill levelObject, LevelData data) {

        super(levelObject, levelObject.getProperties().getLevelFormula(), data);
    }

    @Override
    public void addExp(int exp) {

        super.addExp(exp);
        // lets add some exp to the profession of the skill
        exp = (int) (exp * RaidCraft.getComponent(SkillsPlugin.class).getExperienceConfig().getSkillProfessionExpRate());
        getLevelObject().getProfession().getAttachedLevel().addExp(exp);
    }

    @Override
    public void removeExp(int exp) {

        super.removeExp(exp);
        // lets remove the same amount of exp from the profession
        exp = (int) (exp * RaidCraft.getComponent(SkillsPlugin.class).getExperienceConfig().getSkillProfessionExpRate());
        getLevelObject().getProfession().getAttachedLevel().removeExp(exp);
    }
}
