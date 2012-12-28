package de.raidcraft.skills.api.skill;

import com.avaje.ebean.Ebean;
import de.raidcraft.api.database.Database;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectElement;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.effect.Effect;
import de.raidcraft.skills.api.effect.common.Disarm;
import de.raidcraft.skills.api.effect.common.Silence;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.tables.THeroSkill;
import de.raidcraft.skills.tables.TSkillData;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author Silthus
 */
public abstract class AbstractSkill implements Skill {

    private final Hero hero;
    private final SkillProperties properties;
    private final Profession profession;
    private final SkillInformation information;
    protected final THeroSkill database;
    private String description;
    private final Collection<Skill> strongParents = new HashSet<>();
    private final Collection<Skill> weakParents = new HashSet<>();

    protected AbstractSkill(Hero hero, SkillProperties data, Profession profession, THeroSkill database) {

        this.hero = hero;
        this.properties = data;
        this.database = database;
        this.description = data.getDescription();
        this.profession = profession;
        this.information = data.getInformation();

        load(data.getData());
    }

    @Override
    public void checkUsage() throws CombatException {

        if (this instanceof Passive) {
            throw new CombatException(CombatException.Type.PASSIVE);
        }
        // check common effects here
        if (this.isOfType(EffectType.MAGICAL) && getHero().hasEffect(Silence.class)) {
            throw new CombatException(CombatException.Type.SILENCED);
        }
        if (this.isOfType(EffectType.PHYSICAL) && getHero().hasEffect(Disarm.class)) {
            throw new CombatException(CombatException.Type.DISARMED);
        }
        // lets check the resources of the skill and if the hero has it
        if (this.getTotalManaCost() > getHero().getMana()) {
            throw new CombatException(CombatException.Type.LOW_MANA);
        }
        if (this.getTotalStaminaCost() > getHero().getStamina()) {
            throw new CombatException(CombatException.Type.LOW_STAMINA);
        }
        if (this.getTotalHealthCost() > getHero().getHealth()) {
            throw new CombatException(CombatException.Type.LOW_HEALTH);
        }
        // lets check if the player has the required reagents
        for (ItemStack itemStack : getProperties().getReagents()) {
            if (!getHero().getPlayer().getInventory().contains(itemStack)) {
                throw new CombatException(CombatException.Type.MISSING_REAGENT);
            }
        }
    }

    @Override
    public void substractUsageCost() {

        // substract the mana, health and stamina cost
        if (getTotalManaCost() > 0) hero.setMana(hero.getMana() - getTotalManaCost());
        if (getTotalStaminaCost() > 0) hero.setStamina(hero.getStamina() - getTotalStaminaCost());
        if (getTotalHealthCost() > 0) hero.damage(getTotalHealthCost());
        // keep this last or items will be removed before casting
        // TODO: replace with working util method
        hero.getPlayer().getInventory().removeItem(getProperties().getReagents());
    }

    protected final <E extends Effect> E addEffect(CharacterTemplate target, Class<E> eClass) throws CombatException {

        return target.addEffect(this, getHero(), eClass);
    }

    protected final <E extends Effect> E addEffect(Object source, CharacterTemplate target, Class<E> eClass) throws CombatException {

        return target.addEffect(this, source, eClass);
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

        int stamina = (int) (properties.getStaminaCost()
                + (properties.getStaminaLevelModifier() * hero.getLevel().getLevel())
                + (properties.getProfLevelStaminaCostModifier() * getProfession().getLevel().getLevel()));
        if (stamina > 20) stamina = 20;
        return stamina;
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

    protected <V> void setData(String key, V value) {

        TSkillData data = Ebean.find(TSkillData.class).where().eq("key", key).eq("skill_id", getId()).findUnique();
        if (data == null) {
            data = new TSkillData();
            data.setDataKey(key);
            data.setSkill(database);
        }
        data.setDataValue(value.toString());
        Database.save(data);
    }

    protected void removeData(String key) {

        TSkillData data = Ebean.find(TSkillData.class).where().eq("key", key).eq("skill_id", getId()).findUnique();
        if (data != null) {
            Ebean.delete(data);
        }
    }

    protected String getData(String key) {

        return Ebean.find(TSkillData.class).where().eq("key", key).eq("skill_id", getId()).findUnique().getDataValue();
    }

    protected int getDataInt(String key) {

        return Integer.parseInt(getData(key));
    }

    protected double getDataDouble(String key) {

        return Double.parseDouble(getData(key));
    }

    protected String getDataString(String key) {

        return getData(key);
    }

    protected boolean getDataBool(String key) {

        return Boolean.parseBoolean(getData(key));
    }

    @Override
    public void load(ConfigurationSection data) {
        // implement if needed
    }

    /*/////////////////////////////////////////////////////////////////
    //    There are only getter and (setter) beyond this point
    /////////////////////////////////////////////////////////////////*/

    @Override
    public final int getId() {

        return database.getId();
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
    public boolean isOfType(EffectType type) {

        for (EffectType t : getTypes()) {
            if (t == type) {
                return true;
            }
        }
        return false;
    }

    @Override
    public final EffectElement[] getElements() {

        return information.elements();
    }

    @Override
    public boolean isOfElement(EffectElement element) {

        for (EffectElement el : getElements()) {
            if (el == element) {
                return true;
            }
        }
        return false;
    }

    @Override
    public final Hero getHero() {

        return hero;
    }

    @Override
    public final SkillProperties getProperties() {

        return properties;
    }

    @SuppressWarnings("unused")
    protected final void setDescription(String description) {

        this.description = description;
    }

    @Override
    public final boolean isActive() {

        return getProfession().isActive();
    }

    @Override
    public final boolean isUnlocked() {

        return database.isUnlocked();
    }

    @Override
    public final void unlock() {

        getHero().sendMessage(ChatColor.GREEN + "Skill freigeschaltet: " + ChatColor.AQUA + getFriendlyName());
        database.setUnlocked(true);
        save();
        // apply the skill
        apply();
    }

    @Override
    public final void lock() {

        getHero().sendMessage(ChatColor.RED + "Skill wurde entfernt: " + ChatColor.AQUA + getFriendlyName());
        database.setUnlocked(false);
        save();
        // remove the skill
        remove();
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

        return "[S" + getId() + ":" + getProfession() + ":" + getName() + "]";
    }

    @Override
    public void save() {

        Database.save(database);
    }

    @Override
    public final int compareTo(Skill o) {

        if (getProperties().getRequiredLevel() > o.getProperties().getRequiredLevel()) return 1;
        if (getProperties().getRequiredLevel() == o.getProperties().getRequiredLevel()) return 0;
        return -1;
    }

    @Override
    public boolean equals(Object obj) {

        return obj instanceof Skill
                && ((Skill) obj).getId() != 0 && getId() != 0
                && ((Skill) obj).getId() == getId();
    }
}
