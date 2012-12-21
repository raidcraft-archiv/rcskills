package de.raidcraft.skills.api.skill;

import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.DataMap;
import de.raidcraft.api.database.Database;
import de.raidcraft.skills.api.EffectElement;
import de.raidcraft.skills.api.EffectType;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.tables.THeroSkill;
import org.bukkit.ChatColor;

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
    private final DataMap effectConfig;
    private final SkillInformation information;
    protected final THeroSkill database;
    private String description;
    private final Collection<Skill> strongParents = new HashSet<>();
    private final Collection<Skill> weakParents = new HashSet<>();

    protected AbstractSkill(Hero hero, SkillProperties data, Profession profession, THeroSkill database) {

        this.id = database.getId();
        this.hero = hero;
        this.properties = data;
        this.database = database;
        this.description = data.getDescription();
        this.profession = profession;
        this.information = data.getInformation();

        if (data instanceof ConfigurationBase) {
            // lets load properties from the skill config first and then go thru the profession skill config
            this.effectConfig = ((ConfigurationBase) data).getOverrideDataMap("effects");
        } else {
            effectConfig = null;
        }

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
    public int getTotalCastTime() {

        return (int) (properties.getCastTime()
                        + (properties.getCastTimeLevelModifier() * hero.getLevel().getLevel())
                        + (properties.getProfLevelCastTimeModifier() * getProfession().getLevel().getLevel()));
    }

    @Override
    public void load(DataMap data) {
        // implement if needed
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

        return getProperties().getName().toLowerCase();
    }

    @Override
    public final String getFriendlyName() {

        return getProperties().getFriendlyName();
    }

    @Override
    public final String getDescription() {

        return description
                .replace("%player%", hero.getName())
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
    public final EffectType[] getTypes() {

        return information.types();
    }

    @Override
    public EffectElement[] getElements() {

        return information.elements();
    }

    @Override
    public final Hero getHero() {

        return hero;
    }

    @Override
    public final SkillProperties getProperties() {

        return properties;
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

        return database.isUnlocked();
    }

    @Override
    public void unlock() {

        getHero().sendMessage(ChatColor.GREEN + "Skill freigeschaltet: " + ChatColor.AQUA + getFriendlyName());
        database.setUnlocked(true);
        Database.save(database);
    }

    @Override
    public void lock() {

        getHero().sendMessage(ChatColor.RED + "Skill wurde gesperrt: " + ChatColor.AQUA + getFriendlyName());
        database.setUnlocked(false);
        Database.save(database);
    }

    @Override
    public final Profession getProfession() {

        return profession;
    }

    @Override
    public DataMap getEffectConfiguration() {

        return effectConfig;
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

        return "[S" + getId() + ":" + getName() + "]";
    }

    @Override
    public void save() {

        Database.save(database);
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
