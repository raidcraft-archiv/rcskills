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

    int getId();

    boolean isHidden();

    boolean isEnabled();

    void setEnabled(boolean enabled);

    void checkUsage(SkillAction action) throws CombatException;

    void substractUsageCost(SkillAction action);

    Hero getHolder();

    boolean isActive();

    boolean isUnlocked();

    void unlock();

    void lock();

    double getTotalResourceCost(String resource);

    int getUseExp();

    SkillProperties getSkillProperties();

    Profession getProfession();

    int getRequiredLevel();

    void save();

}
