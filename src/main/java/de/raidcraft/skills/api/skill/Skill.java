package de.raidcraft.skills.api.skill;

import de.raidcraft.api.inheritance.Child;
import de.raidcraft.api.inheritance.Parent;
import de.raidcraft.skills.api.EffectElement;
import de.raidcraft.skills.api.EffectType;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.profession.Profession;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public interface Skill extends Parent, Child<Skill>, Comparable<Skill> {

    public void load(ConfigurationSection data);

    public int getId();

    public String getName();

    public String getFriendlyName();

    public String getDescription();

    public String[] getUsage();

    public void checkUsage() throws CombatException;

    public EffectType[] getTypes();

    public boolean isOfType(EffectType type);

    public EffectElement[] getElements();

    public boolean isOfElement(EffectElement element);

    public Hero getHero();

    public boolean isActive();

    public boolean isUnlocked();

    public void unlock();

    public void lock();

    public int getTotalDamage();

    public int getTotalManaCost();

    public int getTotalStaminaCost();

    public int getTotalHealthCost();

    public int getTotalCastTime();

    public SkillProperties getProperties();

    public Profession getProfession();

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
