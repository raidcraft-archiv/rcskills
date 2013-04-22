package de.raidcraft.skills.api.skill;

import com.sk89q.minecraft.util.commands.CommandContext;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.api.requirement.Requirement;
import de.raidcraft.skills.ProfessionManager;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectElement;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.combat.MagicalAttackType;
import de.raidcraft.skills.api.combat.ProjectileType;
import de.raidcraft.skills.api.combat.action.Attack;
import de.raidcraft.skills.api.combat.action.EntityAttack;
import de.raidcraft.skills.api.combat.action.MagicalAttack;
import de.raidcraft.skills.api.combat.action.RangedAttack;
import de.raidcraft.skills.api.combat.action.SkillAction;
import de.raidcraft.skills.api.combat.callback.EntityAttackCallback;
import de.raidcraft.skills.api.combat.callback.ProjectileCallback;
import de.raidcraft.skills.api.effect.Effect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.resource.Resource;
import de.raidcraft.skills.effects.disabling.Disarm;
import de.raidcraft.skills.effects.disabling.Silence;
import de.raidcraft.skills.items.Weapon;
import de.raidcraft.skills.items.WeaponType;
import de.raidcraft.skills.tables.THeroSkill;
import de.raidcraft.skills.tables.TSkillData;
import de.raidcraft.skills.util.ConfigUtil;
import de.raidcraft.skills.util.HeroUtil;
import de.raidcraft.skills.util.TimeUtil;
import de.raidcraft.util.LocationUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Silthus
 */
public abstract class AbstractSkill implements Skill {

    private final int id;
    private final Hero hero;
    private final SkillProperties properties;
    private final Profession profession;
    private final List<Requirement> requirements = new ArrayList<>();
    private final List<Requirement> useRequirements = new ArrayList<>();
    private final Set<EffectType> effectTypes = new HashSet<>();
    private final Set<EffectElement> effectElements = new HashSet<>();
    // protected final THeroSkill database;
    private String description;
    private long lastCast;
    private boolean unlocked = false;
    private Timestamp unlockTime;

    public AbstractSkill(Hero hero, SkillProperties data, Profession profession, THeroSkill database) {

        this.id = (database == null ? 0 : database.getId());
        this.hero = hero;
        this.properties = data;
        this.description = data.getDescription();
        this.profession = profession;
        this.unlocked = (database != null && database.isUnlocked());
        this.effectTypes.addAll(Arrays.asList(data.getInformation().types()));
        this.effectElements.addAll(Arrays.asList(data.getInformation().elements()));
    }

    @Override
    public final void checkUsage(SkillAction action) throws CombatException {

        if (this instanceof Passive) {
            throw new CombatException(CombatException.Type.PASSIVE);
        }
        if (getHero().isInCombat() && !canUseInCombat()) {
            throw new CombatException(CombatException.Type.NO_COMBAT);
        }
        if (!getHero().isInCombat() && !canUseOutOfCombat()) {
            throw new CombatException(CombatException.Type.COMBAT_ONLY);
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
        Set<WeaponType> requiredWeapons = getProperties().getRequiredWeapons();
        if (requiredWeapons.size() > 0) {
            if (getHero().getWeapon(Weapon.Slot.MAIN_HAND) == null
                    || !requiredWeapons.contains(getHero().getWeapon(Weapon.Slot.MAIN_HAND).getWeaponType())) {
                throw new CombatException("Du kannst diesen Skill nicht mit dieser Waffe ausführen.");
            }
        }
        for (Resource resource : getHero().getResources()) {

            double resourceCost = action.getResourceCost(resource.getName());
            if (isVariableResourceCost(resource.getName()) && resourceCost > resource.getCurrent()) {
                resourceCost = resource.getCurrent();
            }

            switch (getResourceCostType(resource.getName())) {

                case FLAT:
                    if (resourceCost > 0 && resourceCost > resource.getCurrent()) {
                        throw new CombatException("Nicht genug " + resource.getFriendlyName() + ".");
                    }
                    break;
                case PERCENTAGE:
                    int cost = (int) (resource.getMax() * resourceCost);
                    if (resourceCost > 0.0 && cost > resource.getCurrent()) {
                        throw new CombatException("Nicht genug " + resource.getFriendlyName() + ".");
                    }
                    break;
            }

        }
        // lets check if the player has the required reagents
        for (ItemStack itemStack : getProperties().getReagents()) {
            if (!getHero().getPlayer().getInventory().contains(itemStack)) {
                throw new CombatException(CombatException.Type.MISSING_REAGENT);
            }
        }
        // lets check the players use requirements
        for (Requirement requirement : getUseRequirements()) {
            if (!requirement.isMet()) {
                throw new CombatException(requirement.getLongReason());
            }
        }
    }

    @Override
    public final void substractUsageCost(SkillAction action) {

        // substract the mana, health and stamina cost
        for (Resource resource : getHero().getResources()) {

            double resourceCost = action.getResourceCost(resource.getName());
            if (isVariableResourceCost(resource.getName()) && resourceCost > resource.getCurrent()) {
                resourceCost = resource.getCurrent();
            }

            if (resourceCost != 0) {
                switch (getResourceCostType(resource.getName())) {

                    case FLAT:
                        resource.setCurrent((int) (resource.getCurrent() - resourceCost));
                        break;
                    case PERCENTAGE:
                        int newVal = (int) (resource.getCurrent() - resource.getMax() * resourceCost);
                        resource.setCurrent(newVal);
                        break;
                }
            }
        }
        // keep this last or items will be removed before casting
        // TODO: replace with working util method
        hero.getPlayer().getInventory().removeItem(getProperties().getReagents());
        // and lets set the cooldown because it is like a usage cost for further casting
        setLastCast(System.currentTimeMillis());
        // also give the player the defined amount of exp for using the skill
        int useExp = getUseExp();
        if (this instanceof LevelableSkill) {
            ((LevelableSkill) this).getAttachedLevel().addExp(useExp);
        } else {
            getProfession().getAttachedLevel().addExp(useExp);
        }
    }

    private List<Requirement> getUseRequirements() {

        if (useRequirements.size() < 1) {
            useRequirements.addAll(getProperties().loadUseRequirements(this));
        }
        return useRequirements;
    }

    @Override
    public final boolean canUseSkill() {

        try {
            checkUsage(new SkillAction(this));
            return true;
        } catch (CombatException ignored) {
        }
        return false;
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

    protected final List<CharacterTemplate> getNearbyTargets() throws CombatException {

        return getNearbyTargets(true);
    }

    protected final List<CharacterTemplate> getNearbyTargets(boolean friendly) throws CombatException {

        return getHero().getNearbyTargets(getTotalRange(), friendly);
    }

    protected final List<CharacterTemplate> getTargetsInFront() throws CombatException {

        return getHero().getTargetsInFront(getTotalRange());
    }

    protected final List<CharacterTemplate> getTargetsInFront(float degrees) throws CombatException {

        return getHero().getTargetsInFront(getTotalRange(), degrees);
    }

    protected final CharacterTemplate getTarget() throws CombatException {

        return getHero().getTarget(getTotalRange());
    }

    protected final CharacterTemplate getTarget(CommandContext args, boolean self) throws CombatException {

        CharacterTemplate target;
        if (args.argsLength() > 0) {
            try {
                target = RaidCraft.getComponent(SkillsPlugin.class).getCharacterManager().getHero(args.getString(0));
                if (!LocationUtil.isWithinRadius(getHero().getPlayer().getLocation(), target.getEntity().getLocation(), getTotalRange())) {
                    throw new CombatException(CombatException.Type.OUT_OF_RANGE);
                }
            } catch (UnknownPlayerException e) {
                throw new CombatException(e.getMessage());
            }
        } else if (self || getHero().getPlayer().isSneaking()) {
            // self holy
            target = getHero();
        } else {
            target = getTarget();
        }
        return target;
    }

    protected final Location getBlockTarget() throws CombatException {

        return getHero().getBlockTarget(getTotalRange());
    }

    protected final Attack<CharacterTemplate, CharacterTemplate> attack(CharacterTemplate target, int damage) throws CombatException {

        return attack(target, damage, null);
    }

    protected final Attack<CharacterTemplate, CharacterTemplate> attack(CharacterTemplate target) throws CombatException {

        return attack(target, getTotalDamage(), null);
    }

    protected final Attack<CharacterTemplate, CharacterTemplate> attack(CharacterTemplate target, EntityAttackCallback callback) throws CombatException {

        return attack(target, getTotalDamage(), callback);
    }

    protected final Attack<CharacterTemplate, CharacterTemplate> attack(CharacterTemplate target, int damage, EntityAttackCallback callback) throws CombatException {

        EntityAttack attack = new EntityAttack(getHero(), target, damage, callback, getTypes().toArray(new EffectType[getTypes().size()]));
        attack.run();
        return attack;
    }

    protected final <T extends ProjectileCallback> RangedAttack<T> rangedAttack(ProjectileType type) throws CombatException {

        return rangedAttack(type, getTotalDamage(), null);
    }

    protected final <T extends ProjectileCallback> RangedAttack<T> rangedAttack(ProjectileType type, int damage) throws CombatException {

        return rangedAttack(type, damage, null);
    }

    protected final <T extends ProjectileCallback> RangedAttack<T> rangedAttack(ProjectileType type, T callback) throws CombatException {

        return rangedAttack(type, getTotalDamage(), callback);
    }

    protected final <T extends ProjectileCallback> RangedAttack<T> rangedAttack(ProjectileType type, int damage, T callback) throws CombatException {

        RangedAttack<T> attack = new RangedAttack<>(getHero(), type, damage, callback);
        attack.run();
        return attack;
    }

    protected final MagicalAttack magicalAttack(CharacterTemplate target, MagicalAttackType type, int damage, EntityAttackCallback callback) throws CombatException {

        MagicalAttack attack = new MagicalAttack(getHero(), target, damage, callback);
        type.run(attack);
        attack.run();
        return attack;
    }

    protected final MagicalAttack magicalAttack(MagicalAttackType type, int damage, EntityAttackCallback callback) throws CombatException {

        return magicalAttack(getTarget(), type, damage, callback);
    }

    protected final MagicalAttack magicalAttack(MagicalAttackType type, int damage) throws CombatException {

        return magicalAttack(type, damage, null);
    }

    protected final MagicalAttack magicalAttack(CharacterTemplate target, MagicalAttackType type, int damage) throws CombatException {

        return magicalAttack(target, type, damage, null);
    }

    protected final MagicalAttack magicalAttack(MagicalAttackType type, EntityAttackCallback callback) throws CombatException {

        return magicalAttack(type, getTotalDamage(), callback);
    }

    protected final MagicalAttack magicalAttack(MagicalAttackType type) throws CombatException {

        return magicalAttack(type, getTotalDamage(), null);
    }

    protected final MagicalAttack magicalAttack(CharacterTemplate target, int damage, EntityAttackCallback callback) throws CombatException {

        MagicalAttack magicalAttack = new MagicalAttack(getHero(), target, damage, callback);
        magicalAttack.run();
        return magicalAttack;
    }

    protected final MagicalAttack magicalAttack(int damage, EntityAttackCallback callback) throws CombatException {

        return magicalAttack(getTarget(), damage, callback);
    }

    protected final MagicalAttack magicalAttack(EntityAttackCallback callback) throws CombatException {

        return magicalAttack(getTarget(), getTotalDamage(), callback);
    }

    protected final MagicalAttack magicalAttack() throws CombatException {

        return magicalAttack(getTarget(), getTotalDamage(), null);
    }

    protected final MagicalAttack magicalAttack(int damage) throws CombatException {

        return magicalAttack(damage, null);
    }

    protected final MagicalAttack magicalAttack(CharacterTemplate target, EntityAttackCallback callback) throws CombatException {

        return magicalAttack(target, getTotalDamage(), callback);
    }

    protected final MagicalAttack magicalAttack(CharacterTemplate taraget, int damage) throws CombatException {

        return magicalAttack(taraget, damage, null);
    }

    protected final void addPermission(String node) {

        RaidCraft.getPermissions().playerAdd(getHero().getPlayer(), node);
    }

    protected final void removePermission(String node) {

        RaidCraft.getPermissions().playerRemove(getHero().getPlayer(), node);
    }

    protected final BlockFace getFacing() {

        return HeroUtil.yawToFace(getHero().getPlayer().getLocation().getYaw());
    }

    @Override
    public final int getTotalDamage() {

        return (int) ConfigUtil.getTotalValue(this, properties.getDamage());
    }

    @Override
    public final Resource.Type getResourceCostType(String resource) {

        return properties.getResourceType(resource);
    }

    @Override
    public final boolean isVariableResourceCost(String resource) {

        return properties.isVariableResourceCost(resource);
    }

    @Override
    public double getTotalResourceCost(String resource) {

        return properties.getResourceCost(resource)
                + (properties.getResourceCostLevelModifier(resource) * hero.getAttachedLevel().getLevel())
                + (properties.getResourceCostProfLevelModifier(resource) * getProfession().getAttachedLevel().getLevel());
    }

    @Override
    public final int getTotalCastTime() {

        return (int) (ConfigUtil.getTotalValue(this, properties.getCastTime()) * 20.0);
    }

    @Override
    public final int getTotalRange() {

        return (int) ConfigUtil.getTotalValue(this, properties.getRange());
    }

    @Override
    public int getUseExp() {

        return (int) ConfigUtil.getTotalValue(this, properties.getUseExp());
    }

    @Override
    public final double getTotalCooldown() {

        return ConfigUtil.getTotalValue(this, properties.getCooldown());
    }

    @Override
    public final void setRemainingCooldown(double cooldown) {

        setLastCast((long) (System.currentTimeMillis() - cooldown * 1000));
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
    public final boolean isHidden() {

        return getProperties().isHidden();
    }

    protected final <V> void setData(String key, V value) {

        TSkillData data = RaidCraft.getDatabase(SkillsPlugin.class).find(TSkillData.class).where().eq("key", key).eq("skill_id", getId()).findUnique();
        if (data == null) {
            data = new TSkillData();
            data.setDataKey(key);
            data.setSkill(RaidCraft.getDatabase(SkillsPlugin.class).find(THeroSkill.class, getId()));
        }
        data.setDataValue(value.toString());

        // dont save when the player is in a blacklist world
        if (RaidCraft.getComponent(SkillsPlugin.class).isSavingWorld(getHero().getPlayer().getWorld().getName())) {
            RaidCraft.getDatabase(SkillsPlugin.class).save(data);
        }
    }

    protected final void removeData(String key) {

        TSkillData data = RaidCraft.getDatabase(SkillsPlugin.class).find(TSkillData.class).where().eq("key", key).eq("skill_id", getId()).findUnique();
        if (data != null) {
            RaidCraft.getDatabase(SkillsPlugin.class).delete(data);
        }
    }

    protected final String getData(String key) {

        return RaidCraft.getDatabase(SkillsPlugin.class).find(TSkillData.class).where().eq("key", key).eq("skill_id", getId()).findUnique().getDataValue();
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
    public String getDescription() {

        return description
                .replace("%player%", hero.getName())
                .replace("%damage%", getTotalDamage() + "");
    }

    @Override
    public String[] getUsage() {

        return getProperties().getUsage();
    }

    @Override
    public final boolean isEnabled() {

        return properties.isEnabled();
    }

    @Override
    public final void setEnabled(boolean enabled) {

        properties.setEnabled(enabled);
    }

    @Override
    public final boolean canUseInCombat() {

        return properties.canUseInCombat();
    }

    @Override
    public final boolean canUseOutOfCombat() {

        return properties.canUseOutOfCombat();
    }

    @Override
    public final Set<EffectType> getTypes() {

        return effectTypes;
    }

    @Override
    public final void addTypes(EffectType... effectTypes) {

        if (effectTypes == null) return;
        this.effectTypes.addAll(Arrays.asList(effectTypes));
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
    public final Set<EffectElement> getElements() {

        return effectElements;
    }

    @Override
    public final void addElements(EffectElement... effectElements) {

        if (effectElements == null) return;
        this.effectElements.addAll(Arrays.asList(effectElements));
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

        return unlocked;
    }

    @Override
    public final void unlock() {

        getHero().sendMessage(ChatColor.GREEN + "Skill freigeschaltet: " + ChatColor.AQUA + getFriendlyName());
        unlocked = true;
        unlockTime = new Timestamp(System.currentTimeMillis());
        save();
        // lets unlock all linked skills without checking the requirements
        for (Skill skill : getProperties().getLinkedSkills(getHero())) {
            skill.unlock();
        }
        // apply the skill
        apply();
    }

    @Override
    public final void lock() {

        getHero().sendMessage(ChatColor.RED + "Skill wurde entfernt: " + ChatColor.AQUA + getFriendlyName());
        unlocked = false;
        save();
        // lock all linked skills without asking questions
        for (Skill skill : getProperties().getLinkedSkills(getHero())) {
            skill.lock();
        }
        // remove the skill
        remove();
    }

    @Override
    public final Profession getProfession() {

        return profession;
    }

    @Override
    public final List<Requirement> getRequirements() {

        if (requirements.size() < 1) {
            requirements.addAll(getProperties().loadRequirements(this));
        }
        return requirements;
    }

    @Override
    public final boolean isMeetingAllRequirements() {

        for (Requirement requirement : getRequirements()) {
            if (!requirement.isMet()) {
                return false;
            }
        }
        return getProperties().getRequiredLevel() <= getProfession().getAttachedLevel().getLevel();
    }

    @Override
    public final String getResolveReason() {

        for (Requirement requirement : requirements) {
            if (!requirement.isMet()) {
                return requirement.getLongReason();
            }
        }
        if (getProperties().getRequiredLevel() > getProfession().getAttachedLevel().getLevel()) {
            return "Dein " + getProfession().getPath().getFriendlyName() + " Spezialisierungs Level ist zu niedrig.";
        }
        return "Skill kann freigeschaltet werden.";
    }

    @Override
    public void save() {

        THeroSkill skill = RaidCraft.getDatabase(SkillsPlugin.class).find(THeroSkill.class, getId());
        skill.setUnlockTime(unlockTime);
        skill.setUnlocked(isUnlocked());
        // dont save when the player is in a blacklist world
        if (getProfession().getName().equalsIgnoreCase(ProfessionManager.VIRTUAL_PROFESSION)
                || RaidCraft.getComponent(SkillsPlugin.class).isSavingWorld(getHero().getPlayer().getWorld().getName())) {
            RaidCraft.getDatabase(SkillsPlugin.class).save(skill);
        }
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
