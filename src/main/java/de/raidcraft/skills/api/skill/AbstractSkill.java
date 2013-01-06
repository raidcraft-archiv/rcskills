package de.raidcraft.skills.api.skill;

import com.avaje.ebean.Ebean;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.database.Database;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectElement;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.combat.action.Attack;
import de.raidcraft.skills.api.combat.action.EntityAttack;
import de.raidcraft.skills.api.combat.action.MagicalAttack;
import de.raidcraft.skills.api.combat.callback.Callback;
import de.raidcraft.skills.api.effect.Effect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.requirement.Requirement;
import de.raidcraft.skills.effects.disabling.Disarm;
import de.raidcraft.skills.effects.disabling.Silence;
import de.raidcraft.skills.tables.THeroSkill;
import de.raidcraft.skills.tables.TSkillData;
import de.raidcraft.skills.util.TimeUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Silthus
 */
public abstract class AbstractSkill implements Skill {

    private final Hero hero;
    private final SkillProperties properties;
    private final Profession profession;
    private final SkillInformation information;
    private final List<Requirement> requirements = new ArrayList<>();
    protected final THeroSkill database;
    private String description;
    private long lastCast;

    public AbstractSkill(Hero hero, SkillProperties data, Profession profession, THeroSkill database) {

        this.hero = hero;
        this.properties = data;
        this.database = database;
        this.description = data.getDescription();
        this.profession = profession;
        this.information = data.getInformation();
        data.loadRequirements(this);
    }

    @Override
    public final void checkUsage() throws CombatException {

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
        if (isOnCooldown()) {
            throw new CombatException(CombatException.Type.ON_COOLDOWN.getMessage() +
                    " Noch: " + TimeUtil.millisToSeconds(getRemainingCooldown()) + "s");
        }
        // lets check the resources of the skill and if the hero has it
        if (this.getTotalManaCost() > getHero().getResource()) {
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
    public final boolean canUseSkill() {

        try {
            checkUsage();
            return true;
        } catch (CombatException ignored) {
        }
        return false;
    }

    @Override
    public final void substractUsageCost() {

        // substract the mana, health and stamina cost
        if (getTotalManaCost() > 0) hero.setResource(hero.getResource() - getTotalManaCost());
        if (getTotalStaminaCost() > 0) hero.setStamina(hero.getStamina() - getTotalStaminaCost());
        try {
            if (getTotalHealthCost() > 0) new MagicalAttack(getHero(), getHero(), getTotalHealthCost()).run();
        } catch (CombatException ignored) {
        }
        // keep this last or items will be removed before casting
        // TODO: replace with working util method
        hero.getPlayer().getInventory().removeItem(getProperties().getReagents());
        // and lets set the cooldown because it is like a usage cost for further casting
        setLastCast(System.currentTimeMillis());
    }

    @Override
    public final boolean matches(String name) {

        name = name.toLowerCase();
        return getName().contains(name) || getFriendlyName().toLowerCase().contains(name);
    }

    protected final <E extends Effect<S>, S extends Skill> E addEffect(CharacterTemplate target, Class<E> eClass) throws CombatException {

        return target.addEffect(this, (S) this, eClass);
    }

    protected final <E extends Effect<S>, S> E addEffect(S source, CharacterTemplate target, Class<E> eClass) throws CombatException {

        return target.addEffect(this, source, eClass);
    }

    protected Set<CharacterTemplate> getNearbyTargets() throws CombatException {

        return getHero().getNearbyTargets(getTotalRange());
    }

    protected CharacterTemplate getTarget() throws CombatException {

        return getHero().getTarget(getTotalRange());
    }

    protected Location getBlockTarget() throws CombatException {

        return getHero().getBlockTarget(getTotalRange());
    }

    protected Attack<CharacterTemplate, CharacterTemplate> attack(CharacterTemplate target, int damage) throws CombatException {

        return attack(target, damage, null);
    }

    protected Attack<CharacterTemplate, CharacterTemplate> attack(CharacterTemplate target) throws CombatException {

        return attack(target, getTotalDamage(), null);
    }

    protected Attack<CharacterTemplate, CharacterTemplate> attack(CharacterTemplate target, Callback<CharacterTemplate> callback) throws CombatException {

        return attack(target, getTotalDamage(), callback);
    }

    protected Attack<CharacterTemplate, CharacterTemplate> attack(CharacterTemplate target, int damage, Callback<CharacterTemplate> callback) throws CombatException {

        EntityAttack attack = new EntityAttack(getHero(), target, damage, callback, getTypes());
        attack.run();
        return attack;
    }

    protected void addPermission(String node) {

        RaidCraft.getPermissions().playerAdd(getHero().getPlayer(), node);
    }

    protected void removePermission(String node) {

        RaidCraft.getPermissions().playerRemove(getHero().getPlayer(), node);
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
                + (properties.getManaCostLevelModifier() * hero.getLevel().getLevel())
                + (properties.getManaCostProfLevelModifier() * getProfession().getLevel().getLevel()));
    }

    @Override
    public int getTotalStaminaCost() {

        int stamina = (int) (properties.getStaminaCost()
                + (properties.getStaminaCostLevelModifier() * hero.getLevel().getLevel())
                + (properties.getStaminaCostProfLevelModifier() * getProfession().getLevel().getLevel()));
        if (stamina > 20) stamina = 20;
        return stamina;
    }

    @Override
    public int getTotalHealthCost() {

        return (int) (properties.getHealthCost()
                + (properties.getHealthCostLevelModifier() * hero.getLevel().getLevel())
                + (properties.getHealthCostProfLevelModifier() * getProfession().getLevel().getLevel()));
    }

    @Override
    public int getTotalCastTime() {

        return (int) (properties.getCastTime()
                + (properties.getCastTimeLevelModifier() * hero.getLevel().getLevel())
                + (properties.getCastTimeProfLevelModifier() * getProfession().getLevel().getLevel()));
    }

    @Override
    public int getTotalRange() {

        return (int) (properties.getRange()
                + (properties.getRangeLevelModifier() * hero.getLevel().getLevel())
                + (properties.getRangeProfLevelModifier() * getProfession().getLevel().getLevel()));
    }

    @Override
    public double getTotalCooldown() {

        return (properties.getCooldown()
                + (properties.getCooldownLevelModifier() * hero.getLevel().getLevel())
                + (properties.getCooldownProfLevelModifier() * getProfession().getLevel().getLevel()));
    }

    @Override
    public final long getRemainingCooldown() {

        return (long) ((lastCast + (getTotalCooldown() * 1000)) - System.currentTimeMillis());
    }

    @Override
    public final boolean isOnCooldown() {

        return getRemainingCooldown() > 0;
    }

    @Override
    public final void setLastCast(long time) {

        this.lastCast = time;
    }

    @Override
    public boolean isHidden() {

        return getProperties().isHidden();
    }

    protected final <V> void setData(String key, V value) {

        TSkillData data = Ebean.find(TSkillData.class).where().eq("key", key).eq("skill_id", getId()).findUnique();
        if (data == null) {
            data = new TSkillData();
            data.setDataKey(key);
            data.setSkill(database);
        }
        data.setDataValue(value.toString());
        Database.save(data);
    }

    protected final void removeData(String key) {

        TSkillData data = Ebean.find(TSkillData.class).where().eq("key", key).eq("skill_id", getId()).findUnique();
        if (data != null) {
            Ebean.delete(data);
        }
    }

    protected final String getData(String key) {

        return Ebean.find(TSkillData.class).where().eq("key", key).eq("skill_id", getId()).findUnique().getDataValue();
    }

    protected final int getDataInt(String key) {

        return Integer.parseInt(getData(key));
    }

    protected final double getDataDouble(String key) {

        return Double.parseDouble(getData(key));
    }

    protected final String getDataString(String key) {

        return getData(key);
    }

    protected final boolean getDataBool(String key) {

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
    public String getDescription() {

        return description
                .replace("%player%", hero.getName())
                .replace("%damage%", getTotalDamage() + "")
                .replace("%mana-cost%", getTotalManaCost() + "")
                .replace("%health-cost%", getTotalHealthCost() + "")
                .replace("%stamina-cost%", getTotalStaminaCost() + "");
    }

    @Override
    public String[] getUsage() {

        return getProperties().getUsage();
    }

    @Override
    public boolean isEnabled() {

        return properties.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {

        properties.setEnabled(enabled);
    }

    @Override
    public final EffectType[] getTypes() {

        return information.types();
    }

    @Override
    public final boolean isOfType(EffectType type) {

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
    public final boolean isOfElement(EffectElement element) {

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
    public List<Requirement> getRequirements() {

        return requirements;
    }

    @Override
    public void addRequirement(Requirement requirement) {

        requirements.add(requirement);
    }

    @Override
    public boolean isUnlockable() {

        for (Requirement requirement : requirements) {
            if (!requirement.isMet(getHero())) {
                return false;
            }
        }
        return getProperties().getRequiredLevel() <= getProfession().getLevel().getLevel();
    }

    @Override
    public String getUnlockReason() {

        for (Requirement requirement : requirements) {
            if (!requirement.isMet(getHero())) {
                return requirement.getReason(getHero());
            }
        }
        return "Skill kann freigeschaltet werden.";
    }

    @Override
    public void save() {

        Database.save(database);
    }

    @Override
    public void apply() {

        // override if needed
    }

    @Override
    public void remove() {

        // override if needed
    }

    @Override
    public final String toString() {

        return getFriendlyName();
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
