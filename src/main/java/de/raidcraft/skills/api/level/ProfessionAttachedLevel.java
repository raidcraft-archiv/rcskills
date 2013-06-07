package de.raidcraft.skills.api.level;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.persistance.LevelData;
import de.raidcraft.skills.api.profession.Profession;

/**
 * @author Silthus
 */
public class ProfessionAttachedLevel extends ConfigurableAttachedLevel<Profession> {

    public ProfessionAttachedLevel(Profession levelObject, LevelData data) {

        super(levelObject, levelObject.getProperties().getLevelFormula(), data);
    }

    @Override
    public void addExp(int exp) {

        if (getLevelObject().isMastered() && getLevelObject().hasChildren()) {
            for (Profession profession : getLevelObject().getChildren()) {
                if (profession.isActive() && !profession.isMastered()) {
                    profession.getAttachedLevel().addExp(exp);
                }
            }
            return;
        }
        super.addExp(exp);
        // lets add some exp to the profession of the skill
        exp = (int) (exp * RaidCraft.getComponent(SkillsPlugin.class).getExperienceConfig().getProfessionHeroExpRate());
        getLevelObject().getHero().getAttachedLevel().addExp(exp, false);
    }

    @Override
    public void removeExp(int exp) {

        super.removeExp(exp);
        // lets remove the same amount of exp from the profession
        exp = (int) (exp * RaidCraft.getComponent(SkillsPlugin.class).getExperienceConfig().getProfessionHeroExpRate());
        getLevelObject().getHero().getAttachedLevel().removeExp(exp, false);
    }
}
