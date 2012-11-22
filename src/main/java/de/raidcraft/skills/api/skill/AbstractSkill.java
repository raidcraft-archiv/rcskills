package de.raidcraft.skills.api.skill;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillData;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.util.DataMap;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author Silthus
 */
public abstract class AbstractSkill implements Skill {

    private final int id;
    private final SkillInformation information;
    private final Hero hero;
    private final String name;
    private final String friendlyName;
    private final String[] usage;
    private final SkillType[] types;
    private Profession profession;
    private String description;
    private boolean unlocked;
    private int manaCost;
    private double manaLevelModifier;
    private int staminaCost;
    private double staminaLevelModifier;
    private int healthCost;
    private double healthLevelModifier;
    private int requiredLevel;
    private int damage;
    private double damageLevelModifier;
    private double castTime;
    private double castTimeLevelModifier;
    private double duration;
    private double durationLevelModifier;
    private final Collection<Skill> strongParents = new HashSet<>();
    private final Collection<Skill> weakParents = new HashSet<>();

    protected AbstractSkill(Hero hero, SkillData data) {

        this.hero = hero;
        this.id = data.getId();
        this.information = data.getInformation();
        this.profession = data.getProfession();
        this.name = information.name();
        this.friendlyName = data.getFriendlyName();
        this.description = data.getDescription();
        this.usage = data.getUsage();
        this.types = data.getSkillTypes();
        this.unlocked = data.isUnlocked();
        this.manaCost = data.getManaCost();
        this.manaLevelModifier = data.getManaLevelModifier();
        this.staminaCost = data.getStaminaCost();
        this.staminaLevelModifier = data.getStaminaLevelModifier();
        this.healthCost = data.getHealthCost();
        this.healthLevelModifier = data.getHealthLevelModifier();
        this.damage = data.getDamage();
        this.damageLevelModifier = data.getDamageLevelModifier();
        this.castTime = data.getCastTime();
        this.castTimeLevelModifier = data.getCastTimeLevelModifier();
        this.duration = data.getDuration();
        this.durationLevelModifier = data.getDurationLevelModifier();
        this.requiredLevel = data.getRequiredLevel();
        load(data.getData());
    }

    @Override
    public double getTotalDamage() {

        return getDamage() + (getDamageLevelModifier() * getProfession().getLevel().getLevel());
    }

    @Override
    public double getTotalManaCost() {
        //TODO: implement
    }

    @Override
    public double getTotalStaminaCost() {
        //TODO: implement
    }

    @Override
    public double getTotalHealthCost() {
        //TODO: implement
    }

    @Override
    public void load(DataMap data) {
        // override this when needed
    }

    /*/////////////////////////////////////////////////////////////////
    //    There are only getter and (setter) beyond this point
    /////////////////////////////////////////////////////////////////*/

    @Override
    public int getId() {

        return id;
    }

    @Override
    public SkillInformation getInformation() {

        return information;
    }

    @Override
    public SkillType[] getSkillTypes() {

        return types;
    }

    @Override
    public Hero getHero() {

        return hero;
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public String getFriendlyName() {

        return friendlyName;
    }

    protected void setDescription(String description) {

        this.description = description;
    }

    @Override
    public String getDescription() {

        return description;
    }

    @Override
    public String getDescription(Hero hero) {

        return getDescription();
    }

    @Override
    public String[] getUsage() {

        return usage;
    }

    @Override
    public boolean isActive() {

        return getProfession().isActive();
    }

    @Override
    public boolean isUnlocked() {

        return unlocked;
    }

    @Override
    public Profession getProfession() {

        return profession;
    }

    @Override
    public int getManaCost() {

        return manaCost;
    }

    @Override
    public double getManaLevelModifier() {

        return manaLevelModifier;
    }

    @Override
    public int getStaminaCost() {

        return staminaCost;
    }

    @Override
    public double getStaminaLevelModifier() {

        return staminaLevelModifier;
    }

    @Override
    public int getHealthCost() {

        return healthCost;
    }

    @Override
    public double getHealthLevelModifier() {

        return healthLevelModifier;
    }

    @Override
    public int getRequiredLevel() {

        return requiredLevel;
    }

    @Override
    public int getDamage() {

        return damage;
    }

    @Override
    public double getDamageLevelModifier() {

        return damageLevelModifier;
    }

    @Override
    public double getCastTime() {

        return castTime;
    }

    @Override
    public double getCastTimeLevelModifier() {

        return castTimeLevelModifier;
    }

    @Override
    public double getDuration() {

        return duration;
    }

    @Override
    public double getDurationLevelModifier() {

        return durationLevelModifier;
    }

    @Override
    public Collection<Skill> getStrongParents() {

        return strongParents;
    }

    @Override
    public Collection<Skill> getWeakParents() {

        return weakParents;
    }

    @Override
    public void addStrongParent(Skill skill) {

        strongParents.add(skill);
    }

    @Override
    public void addWeakParent(Skill skill) {

        weakParents.add(skill);
    }

    @Override
    public void removeStrongParent(Skill skill) {

        strongParents.remove(skill);
    }

    @Override
    public void removeWeakParent(Skill skill) {

        weakParents.remove(skill);
    }

    @Override
    public String toString() {

        return "[S-" + getClass().getName() + "]" + getName();
    }

    @Override
    public boolean equals(Object obj) {

        return obj instanceof Skill
                && ((Skill) obj).getName().equalsIgnoreCase(getName())
                && ((Skill) obj).getHero().equals(getHero());
    }

    @Override
    public int compareTo(Skill o) {

        if (o.getRequiredLevel() == getRequiredLevel()) return 0;
        if (getRequiredLevel() > o.getRequiredLevel()) return 1;
        return -1;
    }
}
