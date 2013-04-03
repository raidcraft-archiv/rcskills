package de.raidcraft.skills.api.skill;

import de.raidcraft.skills.api.combat.EffectElement;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.combat.action.SkillAction;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.resource.Resource;
import de.raidcraft.skills.requirement.SkillRequirementResolver;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Set;

/**
 * @author Silthus
 */
public interface Skill extends Comparable<Skill>, SkillRequirementResolver {

    public void load(ConfigurationSection data);

    public int getId();

    public String getName();

    public String getFriendlyName();

    public String getDescription();

    public String[] getUsage();

    public boolean isHidden();

    public boolean isEnabled();

    public void setEnabled(boolean enabled);

    public boolean canUseInCombat();

    public boolean canUseOutOfCombat();

    public void checkUsage(SkillAction action) throws CombatException;

    public boolean canUseSkill();

    public void substractUsageCost(SkillAction action);

    public Set<EffectType> getTypes();

    public void addTypes(EffectType... effectTypes);

    public boolean isOfType(EffectType type);

    public Set<EffectElement> getElements();

    public void addElements(EffectElement... effectElements);

    public boolean isOfElement(EffectElement element);

    public Hero getHero();

    public boolean isActive();

    public boolean isUnlocked();

    public void unlock();

    public void lock();

    public int getTotalDamage();

    public double getTotalResourceCost(String resource);

    public Resource.Type getResourceCostType(String resource);

    public boolean isVariableResourceCost(String resource);

    public int getTotalCastTime();

    public int getTotalRange();

    public double getTotalCooldown();

    public void setRemainingCooldown(double cooldown);

    public long getRemainingCooldown();

    public boolean isOnCooldown();

    public void setLastCast(long time);

    public SkillProperties getProperties();

    public Profession getProfession();

    public boolean matches(String name);

    public void save();

    /**
     * Applies the skill to the {@link Hero}. Is called when the skill is first added to the hero.
     */
    public void apply();

    /**
     * Removes the skill from the {@link Hero}. Is called when the skill was removed from the hero.
     */
    public void remove();
}
