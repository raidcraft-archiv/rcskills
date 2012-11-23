package de.raidcraft.skills.api.skill;

import de.raidcraft.api.inheritance.Child;
import de.raidcraft.api.inheritance.Parent;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.util.DataMap;

/**
 * @author Silthus
 */
public interface Skill extends Parent, Child<Skill>, Comparable<Skill> {

    public enum Result {

        CANCELLED(false),
        INVALID_TARGET(true),
        FAIL(false),
        LOW_MANA(true),
        LOW_HEALTH(true),
        LOW_LEVEL(true),
        LOW_STAMINA(true),
        MISSING_REAGENT(true),
        NO_COMBAT(true),
        NORMAL(false),
        ON_GLOBAL_COOLDOWN(false),
        ON_COOLDOWN(true),
        REMOVED_EFFECT(false),
        SKIP_POST_USAGE(false),
        START_DELAY(false);

        private final boolean showMessage;

        private Result(boolean showMessage) {

            this.showMessage = showMessage;
        }

        public boolean showMessage() {

            return showMessage;
        }
    }

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

    public Hero getHero();

    public String getDescription(Hero hero);

    public boolean isActive();

    public boolean isUnlocked();

    public double getTotalDamage();

    public double getTotalManaCost();

    public double getTotalStaminaCost();

    public double getTotalHealthCost();

    public SkillProperties getProperties();

    public Profession getProfession() throws UnknownProfessionException;
}
