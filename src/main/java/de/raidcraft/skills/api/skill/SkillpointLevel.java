package de.raidcraft.skills.api.skill;

import de.raidcraft.skills.api.level.SkillLevel;
import de.raidcraft.skills.api.persistance.LevelData;

/**
 * This is a special level that makes sure that a skill on itself cannot level.
 * Skills can only be leveled directly by putting skillpoints into them.
 * Skillpoints are gained when the {@link de.raidcraft.skills.api.hero.Hero} or {@link de.raidcraft.skills.api.profession.Profession}
 * gains a level. Profession skillpoints can only be given to the specific profession skills.
 * Hero skillpoints can be given to all skills.
 *
 * @author Silthus
 */
public class SkillpointLevel extends SkillLevel {

    public SkillpointLevel(LevelableSkill levelObject, LevelData data) {

        super(levelObject, data);
    }

    @Override
    public void addExp(int exp) {
    }

    @Override
    public void removeExp(int exp) {
    }

    @Override
    public void setExp(int exp) {
    }

    @Override
    public int getMaxExp() {

        return 1;
    }

    @Override
    public int getExp() {

        return 0;
    }

    @Override
    public int getNeededExpForLevel(int level) {

        return 1;
    }
}
