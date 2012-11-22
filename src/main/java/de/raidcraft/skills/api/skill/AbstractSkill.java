package de.raidcraft.skills.api.skill;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillData;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.util.DataMap;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author Silthus
 */
public abstract class AbstractSkill implements Skill {

    private final Hero hero;
    private final SkillProperties properties;
    private final String name;
    private Profession profession;
    private String description;
    private final Collection<Skill> strongParents = new HashSet<>();
    private final Collection<Skill> weakParents = new HashSet<>();

    protected AbstractSkill(Hero hero, SkillData data) {

        this.hero = hero;
        this.profession = data.getProfession();
        this.properties = data;
        this.name = data.getInformation().name();
        this.description = data.getDescription();

        load(data.getData());
    }

    @Override
    public double getTotalDamage() {

        return properties.getDamage()
                + (properties.getDamageLevelModifier() * hero.getLevel().getLevel())
                + (properties.getProfLevelDamageModifier() * profession.getLevel().getLevel());
    }

    @Override
    public double getTotalManaCost() {

        return properties.getManaCost()
                + (properties.getManaLevelModifier() * hero.getLevel().getLevel())
                + (properties.getProfLevelManaCostModifier() * profession.getLevel().getLevel());
    }

    @Override
    public double getTotalStaminaCost() {

        return properties.getStaminaCost()
                + (properties.getStaminaLevelModifier() * hero.getLevel().getLevel())
                + (properties.getProfLevelStaminaCostModifier() * profession.getLevel().getLevel());
    }

    @Override
    public double getTotalHealthCost() {

        return properties.getHealthCost()
                + (properties.getHealthLevelModifier() * hero.getLevel().getLevel())
                + (properties.getProfLevelHealthCostModifier() * profession.getLevel().getLevel());
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

        return getProperties().getId();
    }

    @Override
    public String getFriendlyName() {

        return getProperties().getFriendlyName();
    }

    @Override
    public String[] getUsage() {

        return getProperties().getUsage();
    }

    @Override
    public SkillType[] getSkillTypes() {

        return getProperties().getSkillTypes();
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
    public String getDescription() {

        return description;
    }

    @Override
    public SkillProperties getProperties() {

        return properties;
    }

    protected void setDescription(String description) {

        this.description = description;
    }

    @Override
    public String getDescription(Hero hero) {

        return getDescription();
    }

    @Override
    public boolean isActive() {

        return getProfession().isActive();
    }

    @Override
    public boolean isUnlocked() {

        return getProperties().isUnlocked();
    }

    @Override
    public Profession getProfession() {

        return profession;
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

        if (getProperties().getRequiredLevel() > o.getProperties().getRequiredLevel()) return 1;
        if (getProperties().getRequiredLevel() == o.getProperties().getRequiredLevel()) return 0;
        return -1;
    }
}
