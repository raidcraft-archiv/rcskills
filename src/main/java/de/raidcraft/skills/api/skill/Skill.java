package de.raidcraft.skills.api.skill;

import de.raidcraft.api.config.DataMap;
import de.raidcraft.api.inheritance.Child;
import de.raidcraft.api.inheritance.Parent;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.profession.Profession;

/**
 * @author Silthus
 */
public interface Skill extends Parent, Child<Skill>, Comparable<Skill> {

    public enum Type {

        BUFF,
        COUNTER,
        DARK,
        DAMAGING,
        DEBUFF,
        FIRE,
        ICE,
        INTERRUPT,
        ITEM,
        EARTH,
        FORCE,
        HARMFUL,
        HEAL,
        ILLUSION,
        KNOWLEDGE,
        LIGHT,
        LIGHTNING,
        MANA,
        MOVEMENT,
        PHYSICAL,
        SILENCABLE,
        STEALTHY,
        SUMMON,
        TELEPORT,
        UNBINDABLE;
    }

    public void load(DataMap data);

    public int getId();

    public String getName();

    public String getFriendlyName();

    public String getDescription();

    public String[] getUsage();

    public Skill.Type[] getSkillTypes();

    public Hero getHero();

    public boolean isActive();

    public boolean isUnlocked();

    public void unlock();

    public void lock();

    public int getTotalDamage();

    public int getTotalManaCost();

    public int getTotalStaminaCost();

    public int getTotalHealthCost();

    public SkillProperties getProperties();

    public Profession getProfession();

    public DataMap getEffectConfiguration();

    public void save();

    /**
     * Applies the skill to the {@link Hero}. Is called when the skill is first added to the hero.
     * If {@link de.raidcraft.skills.api.skill.type.Passive} is implemented this method will be
     * called every few ticks.
     *
     * @param hero to apply skill to.
     */
    public void apply(Hero hero);

    /**
     * Removes the skill from the {@link Hero}. Is called when the skill was removed from the hero.
     *
     * @param hero to apply skill to.
     */
    public void remove(Hero hero);
}
