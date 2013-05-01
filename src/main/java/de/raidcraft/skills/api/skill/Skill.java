package de.raidcraft.skills.api.skill;

import de.raidcraft.skills.api.combat.action.SkillAction;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.resource.Resource;
import de.raidcraft.skills.requirement.SkillRequirementResolver;

/**
 * @author Silthus
 */
public interface Skill extends SkillRequirementResolver, Ability<Hero> {

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

    public Resource.Type getResourceCostType(String resource);

    public boolean isVariableResourceCost(String resource);

    public int getUseExp();

    public Profession getProfession();

    public void save();

}
