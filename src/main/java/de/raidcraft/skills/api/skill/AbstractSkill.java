package de.raidcraft.skills.api.skill;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.combat.Effect;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.EffectProperties;
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
    private final Profession profession;
    private String description;
    private boolean unlocked;
    private final Collection<Skill> strongParents = new HashSet<>();
    private final Collection<Skill> weakParents = new HashSet<>();

    protected AbstractSkill(Hero hero, SkillProperties data, THeroSkill database, Profession profession) {

        this.id = database.getId();
        this.hero = hero;
        this.properties = data;
        this.description = data.getDescription();
        this.unlocked = database.isUnlocked();
        this.profession = profession;

        load(data.getData());
    }

    @Override
    public int getTotalDamage() {

        return (int) (properties.getDamage()
                        + (properties.getDamageLevelModifier() * hero.getLevel().getLevel())
                        + (properties.getProfLevelDamageModifier() * getProfession().getLevel().getLevel()));
    }

    @Override
    public int getTotalManaCost() {

        return (int) (properties.getManaCost()
                        + (properties.getManaLevelModifier() * hero.getLevel().getLevel())
                        + (properties.getProfLevelManaCostModifier() * getProfession().getLevel().getLevel()));
    }

    @Override
    public int getTotalStaminaCost() {

        return (int) (properties.getStaminaCost()
                        + (properties.getStaminaLevelModifier() * hero.getLevel().getLevel())
                        + (properties.getProfLevelStaminaCostModifier() * getProfession().getLevel().getLevel()));
    }

    @Override
    public int getTotalHealthCost() {

        return (int) (properties.getHealthCost()
                        + (properties.getHealthLevelModifier() * hero.getLevel().getLevel())
                        + (properties.getProfLevelHealthCostModifier() * getProfession().getLevel().getLevel()));
    }

    @Override
    public int getTotalEffectDuration() {

        EffectProperties properties = getEffectProperties();
        return (int) (properties.getDuration()
                + (properties.getDurationLevelModifier() * hero.getLevel().getLevel())
                + (properties.getDurationProfLevelModifier() * getProfession().getLevel().getLevel()));
    }

    @Override
    public int getTotalEffectInterval() {

        EffectProperties properties = getEffectProperties();
        return (int) (properties.getInterval()
                + (properties.getIntervalLevelModifier() * hero.getLevel().getLevel())
                + (properties.getIntervalProfLevelModifier() * getProfession().getLevel().getLevel()));
    }

    @Override
    public int getTotalEffectDelay() {

        EffectProperties properties = getEffectProperties();
        return (int) (properties.getDelay()
                + (properties.getDelayLevelModifier() * hero.getLevel().getLevel())
                + (properties.getDelayProfLevelModifier() * getProfession().getLevel().getLevel()));
    }

    @Override
    public void load(DataMap data) {
        // override this when needed
    }

    /*/////////////////////////////////////////////////////////////////
    //    Methods that handle applying of effects are here
    /////////////////////////////////////////////////////////////////*/

    public final void addEffect(Effect effect, LivingEntity target) {

        addEffect(effect, getHero(), target);
    }

    public final void addEffect(Effect effect, Hero source, LivingEntity target) {

        // dont add invalid effects
        if (!effect.getClass().isAnnotationPresent(EffectInformation.class)) {
            RaidCraft.LOGGER.warning("The effect " + effect.getClass().getCanonicalName() + " has no EffectInformation!");
            return;
        }
        RaidCraft.getComponent(SkillsPlugin.class).getCombatManager().addEffect(effect, source, target);
    }

    /*/////////////////////////////////////////////////////////////////
    //    There are only getter and (setter) beyond this point
    /////////////////////////////////////////////////////////////////*/

    @Override
    public final int getId() {

        return id;
    }

    @Override
    public final String getName() {

        return getProperties().getName();
    }

    @Override
    public final String getFriendlyName() {

        return getProperties().getFriendlyName();
    }

    @Override
    public final String getDescription() {

        return description
                .replace("%player%", hero.getDisplayName())
                .replace("%damage%", getTotalDamage() + "")
                .replace("%mana-cost%", getTotalManaCost() + "")
                .replace("%health-cost%", getTotalHealthCost() + "")
                .replace("%stamina-cost%", getTotalStaminaCost() + "");
    }

    @Override
    public final String[] getUsage() {

        return getProperties().getUsage();
    }

    @Override
    public final Type[] getSkillTypes() {

        return getProperties().getSkillTypes();
    }

    @Override
    public final Hero getHero() {

        return hero;
    }

    @Override
    public final SkillProperties getProperties() {

        return properties;
    }

    @Override
    public EffectProperties getEffectProperties() {

        return (EffectProperties) properties;
    }

    protected final void setDescription(String description) {

        this.description = description;
    }

    @Override
    public final boolean isActive() {

        return getProfession().isActive();
    }

    @Override
    public boolean isUnlocked() {

        return unlocked;
    }

    @Override
    public final Profession getProfession() {

        return profession;
    }

    @Override
    public final Collection<Skill> getStrongParents() {

        return strongParents;
    }

    @Override
    public final Collection<Skill> getWeakParents() {

        return weakParents;
    }

    @Override
    public final void addStrongParent(Skill skill) {

        strongParents.add(skill);
    }

    @Override
    public final void addWeakParent(Skill skill) {

        weakParents.add(skill);
    }

    @Override
    public final void removeStrongParent(Skill skill) {

        strongParents.remove(skill);
    }

    @Override
    public final void removeWeakParent(Skill skill) {

        weakParents.remove(skill);
    }

    @Override
    public final String toString() {

        return "[S" + getId() + "-" + getClass().getName() + "]" + getName();
    }

    @Override
    public boolean equals(Object obj) {

        return obj instanceof Skill
                && ((Skill) obj).getName().equalsIgnoreCase(getName())
                && ((Skill) obj).getHero().equals(getHero());
    }

    @Override
    public final int compareTo(Skill o) {

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
