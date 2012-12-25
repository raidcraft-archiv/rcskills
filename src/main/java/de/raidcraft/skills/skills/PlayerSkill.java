package de.raidcraft.skills.skills;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;

/**
 * @author Silthus
 */
public final class PlayerSkill {

    private final Skill skill;

    public PlayerSkill(Skill skill) {

        this.skill = skill;
    }

    public int getId() {

        return skill.getId();
    }

    public String getName() {

        return skill.getName();
    }

    public Skill getSkill() {

        return skill;
    }

    public Profession getProfession() {

        return skill.getProfession();
    }

    public Hero getHero() {

        return skill.getHero();
    }

    @Override
    public boolean equals(Object obj) {

        return obj instanceof PlayerSkill && ((PlayerSkill) obj).getId() == getId();
    }
}
