package de.raidcraft.skills.api.skill;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.items.CustomItem;
import de.raidcraft.api.items.CustomItemManager;
import de.raidcraft.api.items.CustomItemStack;
import de.raidcraft.api.items.CustomWeapon;
import de.raidcraft.api.items.WeaponType;
import de.raidcraft.api.requirement.Requirement;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.ability.AbstractAbility;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.combat.action.SkillAction;
import de.raidcraft.skills.api.combat.callback.Callback;
import de.raidcraft.skills.api.effect.common.QueuedAttack;
import de.raidcraft.skills.api.effect.common.QueuedInteract;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Attribute;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.resource.Resource;
import de.raidcraft.skills.effects.disabling.Disarm;
import de.raidcraft.skills.effects.disabling.Silence;
import de.raidcraft.skills.hero.TemporaryHero;
import de.raidcraft.skills.tables.THeroSkill;
import de.raidcraft.skills.trigger.AttackTrigger;
import de.raidcraft.skills.trigger.PlayerInteractTrigger;
import de.raidcraft.skills.util.ConfigUtil;
import de.raidcraft.util.TimeUtil;
import org.bukkit.ChatColor;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author Silthus
 */
@IgnoredSkill
public abstract class AbstractSkill extends AbstractAbility<Hero> implements Skill {

    private final int id;
    private final SkillProperties properties;
    private final Profession profession;
    private final List<Requirement<Hero>> requirements = new ArrayList<>();
    private final List<Requirement<Hero>> useRequirements = new ArrayList<>();
    private boolean unlocked = false;
    private Timestamp unlockTime;

    public AbstractSkill(Hero hero, SkillProperties data, Profession profession, THeroSkill database) {

        super(hero, data);

        this.id = (database == null ? 0 : database.getId());
        this.properties = data;
        this.profession = profession;
        this.unlocked = (database != null && database.isUnlocked());
        this.effectTypes.addAll(Arrays.asList(data.getInformation().types()));
        this.effectElements.addAll(Arrays.asList(data.getInformation().elements()));
    }

    @Override
    public boolean canUseAbility() {

        try {
            checkUsage(new SkillAction(this));
            return true;
        } catch (CombatException ignored) {
        }
        return false;
    }

    private List<Requirement<Hero>> getUseRequirements() {

        if (useRequirements.size() < 1) {
            useRequirements.addAll(getSkillProperties().loadUseRequirements(this));
        }
        return useRequirements;
    }

    @Override
    public double getTotalDamage() {

        double damage = super.getTotalDamage();
        for (Attribute attribute : getHolder().getAttributes()) {
            for (EffectType type : getTypes()) {
                damage += attribute.getBonusDamage(type);
            }
        }
        return damage;
    }

    @Override
    public boolean equals(Object obj) {

        return obj instanceof Skill
                && ((Skill) obj).getId() != 0 && getId() != 0
                && ((Skill) obj).getId() == getId();
    }

    protected final void addPermission(String node) {

        RaidCraft.getPermissions().playerAdd(getHolder().getPlayer(), node);
    }

    protected final void removePermission(String node) {

        RaidCraft.getPermissions().playerRemove(getHolder().getPlayer(), node);
    }


    @Override
    public final int getId() {

        return id;
    }

    @Override
    public final boolean isHidden() {

        return getSkillProperties().isHidden();
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
    public final void checkUsage(SkillAction action) throws CombatException {

        if (this instanceof Passive) {
            throw new CombatException(CombatException.Type.PASSIVE);
        }
        if (getHolder().isInCombat() && !canUseInCombat()) {
            throw new CombatException(CombatException.Type.NO_COMBAT);
        }
        if (!getHolder().isInCombat() && !canUseOutOfCombat()) {
            throw new CombatException(CombatException.Type.COMBAT_ONLY);
        }
        // check common effects here
        if (this.isOfType(EffectType.MAGICAL) && getHolder().hasEffect(Silence.class)) {
            throw new CombatException(CombatException.Type.SILENCED);
        }
        if (this.isOfType(EffectType.PHYSICAL) && getHolder().hasEffect(Disarm.class)) {
            throw new CombatException(CombatException.Type.DISARMED);
        }
        if (isOnCooldown()) {
            throw new CombatException(CombatException.Type.ON_COOLDOWN.getMessage() +
                    " Noch: " +
                    (getRemainingCooldown() > 60.0 ? TimeUtil.secondsToMinutes(getRemainingCooldown()) + "min" : getRemainingCooldown() + "s"));
        }
        Set<WeaponType> requiredWeapons = getSkillProperties().getRequiredWeapons();
        if (!getSkillProperties().getInformation().queuedAttack() && requiredWeapons.size() > 0) {
            CustomItemStack customItem = RaidCraft.getComponent(CustomItemManager.class)
                    .getCustomItem(getHolder().getEntity().getEquipment().getItemInHand());
            if (customItem == null || customItem.getItem() == null) {
                throw new CombatException("Du benötigst eine Waffe um diesen Skill auszuführen.");
            }
            CustomItem item = customItem.getItem();
            if (!(item instanceof CustomWeapon) || !requiredWeapons.contains(((CustomWeapon) item).getWeaponType())) {
                throw new CombatException("Du kannst diesen Skill nicht mit dieser Waffe ausführen.");
            }
        }
        for (Resource resource : getHolder().getResources()) {
            double resourceCost = action.getResourceCost(resource.getName());
            if (resourceCost > 0.0 && resourceCost > resource.getCurrent()) {
                throw new CombatException("Nicht genug " + resource.getFriendlyName() + ".");
            }
        }
        // lets check if the player has the required reagents
        for (ItemStack itemStack : getSkillProperties().getReagents()) {
            if (!getHolder().getPlayer().getInventory().contains(itemStack)) {
                throw new CombatException(CombatException.Type.MISSING_REAGENT);
            }
        }
        // lets check the players use requirements
        for (Requirement<Hero> requirement : getUseRequirements()) {
            if (!requirement.isMet(getHolder())) {
                throw new CombatException(requirement.getLongReason());
            }
        }
    }

    @Override
    public final void substractUsageCost(SkillAction action) {

        // substract the mana, health and stamina cost
        for (Resource resource : getHolder().getResources()) {
            resource.setCurrent(resource.getCurrent() - action.getResourceCost(resource.getName()));
        }
        // keep this last or items will be removed before casting
        holder.getPlayer().getInventory().removeItem(getSkillProperties().getReagents());
        // and lets set the cooldown because it is like a usage cost for further casting
        setLastCast(Instant.now());
        // get the cooldown from the skill action
        setCooldown(action.getCooldown(), false);
        // also give the player the defined amount of exp for using the skill
        int useExp = getUseExp();
        if (this instanceof LevelableSkill) {
            ((LevelableSkill) this).getAttachedLevel().addExp(useExp);
        } else {
            getProfession().getAttachedLevel().addExp(useExp);
        }
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

        if (!isHidden()) {
            getHolder().sendMessage(ChatColor.GREEN + "Skill freigeschaltet: " + ChatColor.AQUA + getFriendlyName());
        }
        unlocked = true;
        unlockTime = new Timestamp(System.currentTimeMillis());
        save();
        // fire the skill gain event
        RaidCraft.callEvent(new PlayerUnlockSkillEvent(this));
        // lets unlock all linked skills without checking the requirements
        for (Skill skill : getSkillProperties().getLinkedSkills(getHolder())) {
            skill.unlock();
        }
        // apply the skill
        apply();
    }

    @Override
    public final void lock() {

        if (!isHidden()) {
            getHolder().sendMessage(ChatColor.RED + "Skill wurde entfernt: " + ChatColor.AQUA + getFriendlyName());
        }
        unlocked = false;
        save();
        // lock all linked skills without asking questions
        for (Skill skill : getSkillProperties().getLinkedSkills(getHolder())) {
            skill.lock();
        }
        // remove the skill
        remove();
    }

    @Override
    public double getTotalResourceCost(String resource) {

        return ConfigUtil.getTotalValue(this, getSkillProperties().getResourceCost(resource));
    }

    @Override
    public int getUseExp() {

        return (int) ConfigUtil.getTotalValue(this, properties.getUseExp());
    }

    @Override
    public SkillProperties getSkillProperties() {

        return properties;
    }

    /*/////////////////////////////////////////////////////////////////
    //    There are only getter and (setter) beyond this point
    /////////////////////////////////////////////////////////////////*/

    @Override
    public final Profession getProfession() {

        return profession;
    }

    @Override
    public void save() {

        if (getHolder() instanceof TemporaryHero) return;
        THeroSkill skill = RaidCraft.getDatabase(SkillsPlugin.class).find(THeroSkill.class, getId());
        if (skill == null) return;
        skill.setUnlockTime(unlockTime);
        skill.setUnlocked(isUnlocked());
        if (getLastCast() != null) skill.setLastCast(Timestamp.from(getLastCast()));
        RaidCraft.getDatabase(SkillsPlugin.class).save(skill);
    }


    protected final QueuedAttack queueAttack(Callback<AttackTrigger> callback) throws CombatException {

        QueuedAttack effect = addEffect(QueuedAttack.class);
        effect.addCallback(callback);
        return effect;
    }

    protected final QueuedInteract queueInteract(Callback<PlayerInteractTrigger> callback) throws CombatException {

        return queueInteract(callback, null);
    }

    protected final QueuedInteract queueInteract(Callback<PlayerInteractTrigger> callback, Action action) throws CombatException {

        QueuedInteract effect = addEffect(QueuedInteract.class);
        effect.addCallback(callback, action);
        return effect;
    }

    @SuppressWarnings("unused")
    protected final void setDescription(String description) {

        this.description = description;
    }

    @Override
    public Hero getObject() {

        return getHolder();
    }

    @Override
    public final List<Requirement<Hero>> getRequirements() {

        if (requirements.size() < 1) {
            requirements.addAll(getSkillProperties().loadRequirements(this));
        }
        return requirements;
    }

    @Override
    public final boolean isMeetingAllRequirements(Hero object) {

        for (Requirement<Hero> requirement : getRequirements()) {
            if (!requirement.isMet(object)) {
                return false;
            }
        }
        return getSkillProperties().getRequiredLevel() <= getProfession().getAttachedLevel().getLevel();
    }

    @Override
    public final String getResolveReason(Hero object) {

        for (Requirement<Hero> requirement : getRequirements()) {
            if (!requirement.isMet(getHolder())) {
                return requirement.getLongReason();
            }
        }
        if (getSkillProperties().getRequiredLevel() > getProfession().getAttachedLevel().getLevel()) {
            return "Dein " + getProfession().getPath().getFriendlyName() + " Spezialisierungs Level ist zu niedrig.";
        }
        return "Skill kann freigeschaltet werden.";
    }

    @Override
    public final int compareTo(Skill o) {

        if (getSkillProperties().getRequiredLevel() > o.getSkillProperties().getRequiredLevel()) return 1;
        if (getSkillProperties().getRequiredLevel() == o.getSkillProperties().getRequiredLevel()) return 0;
        return -1;
    }
}
