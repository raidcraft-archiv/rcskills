package de.raidcraft.skills.api.skill;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.combat.Effect;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.tables.THeroSkill;
import de.raidcraft.util.DataMap;
import org.bukkit.entity.LivingEntity;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author Silthus
 */
public abstract class AbstractSkill implements Skill {

    private final int id;
    private final Hero hero;
    private final SkillProperties properties;
    private String description;
    private boolean unlocked;
    private Profession profession;
    private final String professionName;
    private final Collection<Skill> strongParents = new HashSet<>();
    private final Collection<Skill> weakParents = new HashSet<>();

    protected AbstractSkill(Hero hero, SkillProperties data, THeroSkill database) {

        this.id = database.getId();
        this.hero = hero;
        this.properties = data;
        this.description = data.getDescription();
        this.unlocked = database.isUnlocked();
        this.professionName = database.getProfession().getName();

        load(data.getData());
    }

    @Override
    public double getTotalDamage() {

        return properties.getDamage()
                + (properties.getDamageLevelModifier() * hero.getLevel().getLevel())
                + (properties.getProfLevelDamageModifier() * getProfession().getLevel().getLevel());
    }

    @Override
    public double getTotalManaCost() {

        return properties.getManaCost()
                + (properties.getManaLevelModifier() * hero.getLevel().getLevel())
                + (properties.getProfLevelManaCostModifier() * getProfession().getLevel().getLevel());
    }

    @Override
    public double getTotalStaminaCost() {

        return properties.getStaminaCost()
                + (properties.getStaminaLevelModifier() * hero.getLevel().getLevel())
                + (properties.getProfLevelStaminaCostModifier() * getProfession().getLevel().getLevel());
    }

    @Override
    public double getTotalHealthCost() {

        return properties.getHealthCost()
                + (properties.getHealthLevelModifier() * hero.getLevel().getLevel())
                + (properties.getProfLevelHealthCostModifier() * getProfession().getLevel().getLevel());
    }

    @Override
    public void load(DataMap data) {
        // override this when needed
    }

    /*/////////////////////////////////////////////////////////////////
    //    Methods that handle applying of effects are here
    /////////////////////////////////////////////////////////////////*/

    public void addEffect(Class<? extends Effect> effect, LivingEntity target) {

        addEffect(effect, getHero(), target);
    }

    public void addEffect(Class<? extends Effect> effect, Hero source, LivingEntity target) {

    }

    public void addEffect(Effect effect, LivingEntity target) {

        addEffect(effect, getHero(), target);
    }

    public void addEffect(Effect effect, Hero source, LivingEntity target) {

    }

    /*/////////////////////////////////////////////////////////////////
    //    There are only getter and (setter) beyond this point
    /////////////////////////////////////////////////////////////////*/

    @Override
    public int getId() {

        return id;
    }

    @Override
    public String getName() {

        return getProperties().getName();
    }

    @Override
    public String getFriendlyName() {

        return getProperties().getFriendlyName();
    }

    @Override
    public String getDescription() {

        return description;
    }

    @Override
    public String[] getUsage() {

        return getProperties().getUsage();
    }

    @Override
    public Type[] getSkillTypes() {

        return getProperties().getSkillTypes();
    }

    @Override
    public Hero getHero() {

        return hero;
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

        return unlocked;
    }

    @Override
    public Profession getProfession() {

        if (profession == null) {
            try {
                this.profession = RaidCraft.getComponent(SkillsPlugin.class).getProfessionManager().getProfession(getHero(), professionName);
            } catch (UnknownSkillException e) {
                // this should never occur since we are the skill
                e.printStackTrace();
            } catch (UnknownProfessionException e) {
                // should never occur since everything is already build
                e.printStackTrace();
            }
        }
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

        return "[S" + getId() + "-" + getClass().getName() + "]" + getName();
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

    @Override
    public void apply(Hero hero) {
        // override if needed
    }

    @Override
    public void remove(Hero hero) {
        // override if needed
    }
}
