package de.raidcraft.skills.api.combat.attack;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.Skill;

/**
 * @author Silthus
 */
public class SkillAttack<T> extends AbstractAttack<Hero, T> {

    private final Skill skill;

    protected SkillAttack(Skill skill, T target) {

        super(skill.getHero(), target, skill.getTotalDamage());
        this.skill = skill;
    }

    @Override
    public void run() {
        //TODO: implement
    }
}
