package de.raidcraft.skills.skills.gathering;

import de.raidcraft.skills.api.Passive;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.LevelData;
import de.raidcraft.skills.api.persistance.SkillData;
import de.raidcraft.skills.api.skill.AbstractLevelableSkill;
import de.raidcraft.skills.api.skill.SkillInformation;
import de.raidcraft.skills.api.skill.SkillType;

/**
 * @author Silthus
 */
@SkillInformation(
        name = "excavation",
        desc = "Gräbt die Erde nach Schätzen um.",
        types = {SkillType.UNBINDABLE}
)
public class Excavation extends AbstractLevelableSkill implements Passive {


    public Excavation(Hero hero, SkillData skillData, LevelData levelData) {

        super(hero, skillData, levelData);
    }

    @Override
    public void apply(Hero hero) {


    }
}
