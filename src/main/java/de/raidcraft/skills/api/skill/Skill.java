package de.raidcraft.skills.api.skill;

import de.raidcraft.api.requirement.RequirementResolver;
import de.raidcraft.skills.api.ability.Ability;
import de.raidcraft.skills.api.combat.action.SkillAction;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.profession.Profession;

/**
 * @author Silthus
 */
public interface Skill extends Ability<Hero>, Comparable<Skill>, RequirementResolver<Hero> {

    public int getId();

    public boolean isHidden();

    public boolean isEnabled();

    public void setEnabled(boolean enabled);

    public void checkUsage(SkillAction action) throws CombatException;

    public boolean canUseSkill();

    public void substractUsageCost(SkillAction action);

    public Hero getHolder();

    public boolean isActive();

    public boolean isUnlocked();

    public void unlock();

    public void lock();

    public double getTotalResourceCost(String resource);

    public int getUseExp();

    public SkillProperties getSkillProperties();

    public Profession getProfession();

    public void save();

}
