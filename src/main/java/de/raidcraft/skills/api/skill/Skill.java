package de.raidcraft.skills.api.skill;

import de.raidcraft.api.action.requirement.RequirementResolver;
import de.raidcraft.skills.api.ability.Ability;
import de.raidcraft.skills.api.combat.action.SkillAction;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.profession.Profession;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
@IgnoredSkill
public interface Skill extends Ability<Hero>, Comparable<Skill>, RequirementResolver<Player> {

    public int getId();

    public boolean isHidden();

    public boolean isEnabled();

    public void setEnabled(boolean enabled);

    void checkUsage(SkillAction action) throws CombatException;

    void substractUsageCost(SkillAction action);

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
