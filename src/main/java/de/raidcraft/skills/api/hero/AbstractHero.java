package de.raidcraft.skills.api.hero;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.events.RCPlayerChangedProfessionEvent;
import de.raidcraft.api.events.RCPlayerGainExpEvent;
import de.raidcraft.api.items.*;
import de.raidcraft.skills.CharacterManager;
import de.raidcraft.skills.ProfessionManager;
import de.raidcraft.skills.Scoreboards;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.AbstractSkilledCharacter;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.character.CharacterType;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.level.AttachedLevel;
import de.raidcraft.skills.api.level.ConfigurableAttachedLevel;
import de.raidcraft.skills.api.level.ExpPool;
import de.raidcraft.skills.api.party.Party;
import de.raidcraft.skills.api.path.Path;
import de.raidcraft.skills.api.persistance.HeroData;
import de.raidcraft.skills.api.profession.AbstractProfession;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.resource.Resource;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.ui.BukkitUserInterface;
import de.raidcraft.skills.api.ui.UserInterface;
import de.raidcraft.skills.binds.BindManager;
import de.raidcraft.skills.config.LevelConfig;
import de.raidcraft.skills.config.ProfessionConfig;
import de.raidcraft.skills.formulas.FormulaType;
import de.raidcraft.skills.hero.TemporaryHero;
import de.raidcraft.skills.tables.THero;
import de.raidcraft.skills.tables.THeroSkill;
import de.raidcraft.skills.util.ConfigUtil;
import de.raidcraft.skills.util.ItemUtil;
import de.raidcraft.skills.util.StringUtils;
import de.raidcraft.util.CaseInsensitiveMap;
import de.raidcraft.util.CustomItemUtil;
import de.raidcraft.util.MathUtil;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * @author Silthus
 */
public abstract class AbstractHero extends AbstractSkilledCharacter<Hero> implements Hero {

    @Getter
    private final int id;

    private String name;
    private final AttachedLevel<Hero> expPool;
    private final HeroOptions options;
    private UserInterface userInterface;
    private final Map<String, Profession> professions = new CaseInsensitiveMap<>();
    private final Map<String, Resource> resources = new CaseInsensitiveMap<>();
    private final Map<String, Attribute> attributes = new CaseInsensitiveMap<>();
    private final Map<String, Path<Profession>> paths = new CaseInsensitiveMap<>();
    private Party pendingPartyInvite;
    private Profession virtualProfession;
    // this just tells the client what to display in the experience bar and so on
    private Profession selectedProfession;
    private Profession highestRankedProfession;
    private long lastCombatAction;
    private boolean pvpEnabled = false;

    @Getter
    private BindManager bindings;

    protected AbstractHero(OfflinePlayer player, HeroData data) {

        super(player.isOnline() ? player.getPlayer() : null);

        this.id = data.getId();
        this.name = player.getName();
        this.expPool = new ExpPool(this, data.getExpPool());
        this.options = new HeroOptions(this);
        this.maxLevel = data.getMaxLevel();
        this.bindings = new BindManager(this);

        // load some default options
        pvpEnabled = Option.PVP.isSet(this);
        // level needs to be attached fast to avoid npes when loading the skills
        ConfigurationSection levelConfig = RaidCraft.getComponent(SkillsPlugin.class).getLevelConfig()
                .getConfigFor(LevelConfig.Type.HEROES, getName());
        FormulaType formulaType = FormulaType.fromName(levelConfig.getString("type", "wow"));
        attachLevel(new ConfigurableAttachedLevel<>(this, formulaType.create(levelConfig), data.getLevelData()));
        // load the professions first so we have the skills already loaded
        loadProfessions(data);
        loadAttributes();
        // keep this last because we need to professions to load first
        recalculateHealth();
        setHealth(RaidCraft.getDatabase(SkillsPlugin.class).find(THero.class, getId()).getHealth());
        // load the skills after the profession
        loadSkills();
        if (player.isOnline()) {
            // load the bindings after the skills
            getBindings().load();
            // it is important to load the user interface last or lese it will run in an endless loop
            this.userInterface = new BukkitUserInterface(this);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadProfessions(HeroData data) {

        ProfessionManager manager = RaidCraft.getComponent(SkillsPlugin.class).getProfessionManager();
        for (String professionName : data.getProfessionNames()) {
            try {
                Profession profession = manager.getProfession(this, professionName);
                professions.put(profession.getProperties().getName(), profession);
                paths.put(profession.getPath().getName().toLowerCase(), profession.getPath());
                // set selected profession
                updateHighestRankedProfession();
                // also add the parent if one exists
                if (profession.getParent() != null) {
                    professions.put(profession.getParent().getName(), profession.getParent());
                }
                // add all child professions to our list
                for (Profession child : profession.getChildren()) {
                    professions.put(child.getName(), child);
                }
            } catch (UnknownSkillException | UnknownProfessionException e) {
                RaidCraft.LOGGER.warning("Error while loading the hero professions of " + getName() + ": " + e.getMessage());
            }
        }
        if (virtualProfession == null) {
            this.virtualProfession = manager.getVirtualProfession(this);
        }
        setSelectedProfession(professions.get(data.getSelectedProfession()));
        if (selectedProfession == null) {
            setSelectedProfession(getVirtualProfession());
        }
    }

    private void loadAttributes() {

        attributes.clear();
        for (Profession profession : getProfessions()) {
            if (profession.isActive()) {
                ProfessionConfig config = RaidCraft.getComponent(SkillsPlugin.class).getProfessionManager().getFactory(profession).getConfig();
                ConfigurationSection section = config.getConfigurationSection("attributes");
                if (section == null) {
                    continue;
                }
                for (String key : section.getKeys(false)) {
                    ConfigurationSection attributeSection = section.getConfigurationSection(key);
                    double baseValue = ConfigUtil.getTotalValue(profession, attributeSection.getConfigurationSection("base-value"));
                    ConfigurableAttribute attribute = new ConfigurableAttribute(this, key, (int) baseValue, attributeSection);
                    attributes.put(attribute.getName(), attribute);
                }
            }
        }
    }

    private void removeAttributes(Collection<ItemAttribute> attributes) {

        for (ItemAttribute attribute : attributes) {
            Attribute attr = getAttribute(attribute);
            if (attr != null) {
                attr.removeValue(attribute.getValue());
            }
        }
    }

    @Override
    public double getDefaultHealth() {

        List<Profession> professions = getActiveProfessions();
        double health = 20.0;
        for (Profession profession : professions) {
            double profHealth = ConfigUtil.getTotalValue(profession, profession.getProperties().getBaseHealth());
            if (profHealth > health) {
                health = profHealth;
            }
        }
        return health;
    }

    protected List<Profession> getActiveProfessions() {

        ArrayList<Profession> professions = new ArrayList<>();
        for (Profession profession : getProfessions()) {
            if (profession.isActive()) {
                professions.add(profession);
            }
        }
        return professions;
    }

    @Override
    public boolean equals(Object obj) {

        return obj instanceof Hero
                && ((Hero) obj).getName().equals(getName());
    }

    @Override
    public String getName() {

        if (isOnline()) {
            return getPlayer().getName();
        }
        return name;
    }

    @Override
    public void updateEntity(LivingEntity entity) {

        super.updateEntity(entity);
        if (isOnline()) {
            updatePermissions();
            getUserInterface().refresh();
        }
    }

    @Override
    public CharacterType getCharacterType() {

        return CharacterType.PLAYER;
    }

    @Override
    public boolean hasWeaponsEquiped() {

        return super.hasWeaponsEquiped() && getPlayer().getInventory().getHeldItemSlot() == 0;
    }

    @Override
    public boolean setWeapon(CustomItemStack item) {

        if (super.setWeapon(item)) {
            addAttributes(item.getAttributes());
            return true;
        }
        return false;
    }

    @Override
    public CustomItemStack removeWeapon(EquipmentSlot slot) {

        CustomItemStack weapon = super.removeWeapon(slot);
        if (weapon != null) removeAttributes(weapon.getAttributes());
        return weapon;
    }

    @Override
    public int swingWeapons() {

        int damage = super.swingWeapons();
        for (Attribute attribute : getAttributes()) {
            damage += attribute.getBonusDamage(EffectType.PHYSICAL);
        }
        return damage;
    }

    @Override
    public boolean setArmor(CustomItemStack item) {

        if (super.setArmor(item)) {
            addAttributes(item.getAttributes());
            return true;
        }
        return false;
    }

    @Override
    public CustomItemStack removeArmor(EquipmentSlot slot) {

        CustomItemStack armor = super.removeArmor(slot);
        if (armor != null) removeAttributes(armor.getAttributes());
        return armor;
    }

    @Override
    public double getDamage() {

        double damage = super.getDamage();
        for (Attribute attribute : getAttributes()) {
            damage += attribute.getBonusDamage(EffectType.DEFAULT_ATTACK);
        }
        return damage;
    }

    @Override
    public void setHealth(double health) {

        super.setHealth(health);
        if (getUserInterface() != null) {
            getUserInterface().refresh();
        }
    }

    @Override
    public void increaseMaxHealth(double amount) {

        amount = MathUtil.trim(amount);
        double healthIncreasePercent = MathUtil.toPercent(amount / getMaxHealth());
        super.increaseMaxHealth(amount);
        combatLog("Maximale Leben um " + healthIncreasePercent + "% (" + (amount) + ") erhöht.");
    }

    @Override
    public void decreaseMaxHealth(double amount) {

        amount = MathUtil.trim(amount);
        double healthIncreasePercent = MathUtil.toPercent(amount / getMaxHealth());
        super.decreaseMaxHealth(amount);
        combatLog("Maximale Leben um " + healthIncreasePercent + "% (" + (amount) + ") verringert.");
    }

    @Override
    public boolean isFriendly(CharacterTemplate source) {

        if (source instanceof Hero) {
            return super.isFriendly(source) || (!isPvPEnabled() && !((Hero) source).isPvPEnabled());
        }
        return super.isFriendly(source);
    }

    @Override
    public void setInCombat(boolean inCombat) {

        if (inCombat != isInCombat()) {
            super.setInCombat(inCombat);
            updateSelectedProfession();
            lastCombatAction = System.currentTimeMillis();
        }
    }

    @Override
    public void reset() {

        if (!isOnline()) {
            return;
        }
        setHealth(getMaxHealth());
        for (Resource resource : getResources()) {
            resource.setCurrent(resource.getMax());
        }
        getUserInterface().refresh();
        debug("Reseted all active stats to max");
    }

    @Override
    public void saveLevelProgress(AttachedLevel<CharacterTemplate> attachedLevel) {

        if (this instanceof TemporaryHero) return;
        THero heroTable = RaidCraft.getDatabase(SkillsPlugin.class).find(THero.class, getId());
        heroTable.setExp(getAttachedLevel().getExp());
        heroTable.setLevel(getAttachedLevel().getLevel());

        // dont save when the player is in a blacklist world
        RaidCraft.getDatabase(SkillsPlugin.class).save(heroTable);
    }

    @Override
    public void onExpLoss(int exp) {

        RaidCraft.callEvent(new RCPlayerGainExpEvent(getPlayer(), -exp));
    }

    @Override
    public void onExpGain(int exp) {

        RaidCraft.callEvent(new RCPlayerGainExpEvent(getPlayer(), exp));
    }

    private void updateSelectedProfession() {

        Profession newProfession = null;
        for (Profession profession : getProfessions()) {
            if (!profession.isActive()) {
                continue;
            }
            if (profession.getPath().isSelectedInCombat() && isInCombat()
                    || profession.getPath().isSelectedOutOfCombat() && !isInCombat()) {
                if (newProfession == null
                        || newProfession.getPath().getPriority() < profession.getPath().getPriority()) {
                    newProfession = profession;
                }
            }
        }
        if (newProfession != null) {
            setSelectedProfession(newProfession);
        }
    }

    private void loadSkills() {

        // check all the profession skills for unlock
        // apply all skills that are already unlocked
        professions.values().stream().filter(Profession::isActive).forEach(profession -> {
            // apply all skills that are already unlocked
            profession.getSkills().stream().filter(Skill::isUnlocked).forEach(Skill::apply);
            profession.checkSkillsForUnlock();
        });
        // make sure all virtual skills are added last and override normal skills
        getVirtualProfession().getSkills().stream().filter(Skill::isUnlocked).forEach(Skill::apply);
        getVirtualProfession().checkSkillsForUnlock();
    }

    @Override
    public Player getPlayer() {

        return (Player) getEntity();
    }

    @Override
    public boolean isOnline() {

        return getPlayer() != null && getPlayer().isOnline();
    }

    @Override
    public AttachedLevel<Hero> getExpPool() {

        return expPool;
    }

    @Override
    public UserInterface getUserInterface() {

        return userInterface;
    }

    @Override
    public Collection<Attribute> getAttributes() {

        return attributes.values();
    }

    @Override
    public Attribute getAttribute(String attribute) {

        return attributes.get(StringUtils.formatName(attribute));
    }

    @Override
    public Attribute getAttribute(ItemAttribute attribute) {

        return attributes.get(attribute.getName());
    }

    @Override
    public boolean isAllowedWeapon(WeaponType type) {

        for (Profession profession : getActiveProfessions()) {
            Map<WeaponType, Integer> weapons = profession.getProperties().getAllowedWeapons();
            if (weapons.containsKey(type)) {
                return weapons.get(type) <= profession.getAttachedLevel().getLevel();
            }
        }
        return false;
    }

    @Override
    public boolean isAllowedArmor(ArmorType type) {

        for (Profession profession : getActiveProfessions()) {
            Map<ArmorType, Integer> armor = profession.getProperties().getAllowedArmor();
            if (armor.containsKey(type)) {
                return armor.get(type) <= profession.getAttachedLevel().getLevel();
            }
        }
        return false;
    }

    @Override
    public void checkWeapons() {

        try {
            if (!isOnline()) {
                return;
            }
            // lets check all equiped weapons and adjust the player accordingly
            ItemStack mainWeaponItemStack = getPlayer().getInventory().getItem(CustomItemUtil.MAIN_WEAPON_SLOT);
            if (!CustomItemUtil.isWeapon(mainWeaponItemStack)) {
                removeWeapon(EquipmentSlot.ONE_HANDED);
                removeWeapon(EquipmentSlot.TWO_HANDED);
                return;
            }
            // lets check the durability of the weapon
            CustomItemStack mainWeaponCustomItemStack = RaidCraft.getCustomItem(mainWeaponItemStack);
            if (mainWeaponCustomItemStack.getCustomDurability() < 1) {
                CustomItemUtil.moveItem(getPlayer(), CustomItemUtil.MAIN_WEAPON_SLOT, mainWeaponItemStack);
                removeWeapon(EquipmentSlot.ONE_HANDED);
                removeWeapon(EquipmentSlot.TWO_HANDED);
                throw new CombatException("Diese Waffe ist kaputt und kann nicht angelegt werden. Bitte lasse sie reparieren.");
            }
            CustomWeapon mainWeapon = (CustomWeapon) mainWeaponCustomItemStack.getItem();
            // lets check if the player is allowed to wear the weapon
            if (!isAllowedWeapon(mainWeapon.getWeaponType())) {
                CustomItemUtil.moveItem(getPlayer(), CustomItemUtil.MAIN_WEAPON_SLOT, mainWeaponItemStack);
                CustomItemUtil.setEquipmentTypeColor(getPlayer(), mainWeaponItemStack, ChatColor.RED);
                removeWeapon(EquipmentSlot.ONE_HANDED);
                removeWeapon(EquipmentSlot.TWO_HANDED);
                throw new CombatException("Du kannst diese Waffe nicht tragen.");
            }
            if (mainWeapon.getEquipmentSlot() == EquipmentSlot.SHIELD_HAND) {
                removeWeapon(EquipmentSlot.ONE_HANDED);
                removeWeapon(EquipmentSlot.TWO_HANDED);
                throw new CombatException("Du kannst diese Waffe nur in deiner Schildhand tragen.");
            }
            if (!mainWeapon.isMeetingAllRequirements(getPlayer())) {
                removeWeapon(EquipmentSlot.ONE_HANDED);
                removeWeapon(EquipmentSlot.TWO_HANDED);
                throw new CombatException(mainWeapon.getResolveReason(getPlayer()));
            }
            if (mainWeapon.getEquipmentSlot() == EquipmentSlot.TWO_HANDED) {
                ItemStack secondHandItem = getPlayer().getInventory().getItemInOffHand();
                if (secondHandItem != null && secondHandItem.getType() != Material.AIR) {
                    CustomItemUtil.moveItem(getPlayer(), CustomItemUtil.OFFHAND_WEAPON_SLOT, secondHandItem);
                    removeWeapon(EquipmentSlot.SHIELD_HAND);
                    removeArmor(EquipmentSlot.SHIELD_HAND);
                    sendMessage(ChatColor.RED + "Deine Off-Hand Waffe wurde in dein Inventar gelegt um Platz für deine Zweihand Waffe zu machen.");
                }
                setWeapon(mainWeaponCustomItemStack);
            } else if (mainWeapon.getEquipmentSlot() == EquipmentSlot.ONE_HANDED) {
                setWeapon(mainWeaponCustomItemStack);
                // check for a second weapon too
                CustomItemStack offHandItemStack = RaidCraft.getCustomItem(getPlayer().getInventory().getItemInOffHand());
                if (offHandItemStack != null && offHandItemStack.getType() != Material.AIR) {
                    if (CustomItemUtil.isOffhandWeapon(offHandItemStack)) {
                        CustomWeapon offHandWeapon = CustomItemUtil.getWeapon(offHandItemStack);
                        if (!isAllowedWeapon(offHandWeapon.getWeaponType())) {
                            CustomItemUtil.moveItem(getPlayer(), CustomItemUtil.OFFHAND_WEAPON_SLOT, offHandItemStack);
                            CustomItemUtil.setEquipmentTypeColor(getPlayer(), offHandItemStack, ChatColor.RED);
                            throw new CombatException("Du kannst diese Waffe nicht tragen.");
                        }
                        if (!offHandWeapon.isMeetingAllRequirements(getPlayer())) {
                            throw new CombatException(offHandWeapon.getResolveReason(getPlayer()));
                        }
                        setWeapon(offHandItemStack);
                    } else if (CustomItemUtil.isShield(offHandItemStack)) {
                        // check for a shield
                        CustomArmor armor = CustomItemUtil.getArmor(offHandItemStack);
                        if (!isAllowedArmor(armor.getArmorType())) {
                            CustomItemUtil.moveItem(getPlayer(), CustomItemUtil.OFFHAND_WEAPON_SLOT, offHandItemStack);
                            CustomItemUtil.setEquipmentTypeColor(getPlayer(), offHandItemStack, ChatColor.RED);
                            throw new CombatException("Du kannst diesen Schild nicht tragen.");
                        }
                        if (!armor.isMeetingAllRequirements(getPlayer())) {
                            throw new CombatException(armor.getResolveReason(getPlayer()));
                        }
                        setArmor(offHandItemStack);
                    } else {
                        removeWeapon(EquipmentSlot.SHIELD_HAND);
                        removeArmor(EquipmentSlot.SHIELD_HAND);
                    }
                }
            }
        } catch (CombatException e) {
            sendMessage(ChatColor.RED + e.getMessage());
        }
    }

    @Override
    public void checkArmor() {

        if (!isOnline()) {
            return;
        }
        ItemStack[] armorContents = getPlayer().getInventory().getArmorContents();
        for (int i = 0; i < armorContents.length; i++) {
            if (armorContents[i] == null || armorContents[i].getType() == Material.AIR) {
                removeArmor(EquipmentSlot.fromArmorSlotIndex(i));
            }
            if (CustomItemUtil.isArmor(armorContents[i])) {
                CustomItemStack customItemStack = RaidCraft.getCustomItem(armorContents[i]);
                // check durability
                if (customItemStack.getCustomDurability() < 1) {
                    CustomItemUtil.denyItem(getPlayer(), i + CustomItemUtil.ARMOR_SLOT, customItemStack,
                            "Eine Rüstung von dir ist kaputt und muss repariert werden. Sie wurde in dein Inventar gelegt.");
                    removeArmor(EquipmentSlot.fromArmorSlotIndex(i));
                    // silently continue and dont award armor
                    continue;
                }
                CustomArmor armor = (CustomArmor) customItemStack.getItem();
                if (!isAllowedArmor(armor.getArmorType())) {
                    CustomItemUtil.denyItem(getPlayer(), i + CustomItemUtil.ARMOR_SLOT, customItemStack,
                            "Du kannst diese Rüstung nicht tragen. Sie wurde zurück in dein Inventar gelegt.");
                    removeArmor(EquipmentSlot.fromArmorSlotIndex(i));
                    CustomItemUtil.setEquipmentTypeColor(getPlayer(), customItemStack, ChatColor.RED);
                    continue;
                }
                if (!armor.isMeetingAllRequirements(getPlayer())) {
                    removeArmor(EquipmentSlot.fromArmorSlotIndex(i));
                    CustomItemUtil.denyItem(getPlayer(), i + CustomItemUtil.ARMOR_SLOT, customItemStack,
                            armor.getResolveReason(getPlayer()));
                } else {
                    setArmor(customItemStack);
                }
            }
        }
    }

    @Override
    public Party getPendingPartyInvite() {

        return pendingPartyInvite;
    }

    @Override
    public void setPendingPartyInvite(Party partyInvite) {

        this.pendingPartyInvite = partyInvite;
    }

    @Override
    public int getAttributeValue(String attribute) {

        return getAttribute(attribute).getCurrentValue();
    }

    @Override
    public void setAttributeValue(String attribute, int value) {

        getAttribute(attribute).setCurrentValue(value);
    }

    @Override
    public HeroOptions getOptions() {

        return options;
    }

    @Override
    public void debug(String message) {

        if (Option.DEBUGGING.isSet(this) && message != null && !message.equals("")) {
            getPlayer().sendMessage(ChatColor.GRAY + "[DEBUG] " + message);
        }
    }

    @Override
    public void combatLog(String message) {

        combatLog(null, message);
    }

    @Override
    public void combatLog(Object o, String message) {

        if (Option.COMBAT_LOGGING.isSet(this) && message != null && !message.equals("")) {
            getPlayer().sendMessage(ChatColor.DARK_GRAY + "[Combat]" + (o != null ? "[" + o + "]" : "")
                    + " " + message);
        }
    }

    @Override
    public boolean isPvPEnabled() {

        return pvpEnabled;
    }

    @Override
    public void setPvPEnabled(boolean enablePvP) {

        this.pvpEnabled = enablePvP;
        CharacterManager.refreshPlayerTag(this);
    }

    @Override
    public long getLastCombatAction() {

        return lastCombatAction;
    }

    @Override
    public Resource getResource(String name) {

        return resources.get(name);
    }

    @Override
    public boolean hasResource(String name) {

        return resources.containsKey(name);
    }

    @Override
    public void attachResource(Resource resource) {

        if (resource.getProfession().isActive()) {
            resource.setEnabled(true);
            resources.put(resource.getName(), resource);
        }
    }

    @Override
    public Resource detachResource(String name) {

        Resource resource = resources.remove(name);
        if (resource != null) {
            resource.setEnabled(false);
        }
        return resource;
    }

    @Override
    public Set<Resource> getResources() {

        return new HashSet<>(resources.values());
    }

    @Override
    public Profession getHighestRankedProfession() {

        if (highestRankedProfession == null) {
            updateHighestRankedProfession();
        }
        return highestRankedProfession;
    }

    @Override
    public Profession getSelectedProfession() {

        if (selectedProfession == null) {
            setSelectedProfession(getVirtualProfession());
        }
        return selectedProfession;
    }

    public void setSelectedProfession(Profession profession) {

        this.selectedProfession = profession;
        if (getUserInterface() != null) {
            getUserInterface().refresh();
        }
    }

    @Override
    public Profession getVirtualProfession() {

        return virtualProfession;
    }

    @Override
    public int getPlayerLevel() {

        Profession profession = getHighestRankedProfession();
        return profession.getPath().getTotalPathLevel(this);
    }

    @Override
    public boolean hasPath(Path path) {

        return getPaths().contains(path);
    }

    @Override
    public boolean hasPath(String path) {
        return getPaths().stream().anyMatch(professionPath -> professionPath.getName().equalsIgnoreCase(path));
    }

    @Override
    public Set<Path<Profession>> getPaths() {

        return new HashSet<>(paths.values());
    }

    @Override
    public Path<Profession> getPath(String name) {

        return paths.get(name.toLowerCase());
    }

    @Override
    public void changeProfession(final Profession profession) {

        // lets save once before we change it all to make sure the levels are safed
        save();
        // now lets check what we actually need to change
        // first lets check what path we are changing to and disable all professions on that path
        Path path = profession.getPath();
        for (Profession currentProf : getProfessions()) {
            if (currentProf.getPath().equals(path)) {
                for (Resource resource : currentProf.getResources()) {
                    detachResource(resource.getName());
                }
                currentProf.setActive(false);
                currentProf.save();
            }
        }
        // lets set the selected profession before we go all wanky in the while loop
        updateHighestRankedProfession();

        Profession tmpProfession = profession;
        // now lets go thru all of the professions parents add them and activate them
        do {
            tmpProfession.setActive(true);
            professions.put(tmpProfession.getName(), tmpProfession);
            if (tmpProfession instanceof AbstractProfession) {
                ((AbstractProfession) tmpProfession).loadResources();
                ((AbstractProfession) tmpProfession).loadSkills();
            }
            tmpProfession.save();
            tmpProfession = tmpProfession.getParent();
        } while (tmpProfession != null);

        // update the display stuff
        updateHighestRankedProfession();
        updateSelectedProfession();
        // lets clear all skills from the list and add them again for the profession
        loadAttributes();
        // keep this last because we need to professions to load first
        recalculateHealth();
        // load the skills after the profession
        loadSkills();
        // reload the bound items
        getBindings().reload();
        clearWeapons();
        Scoreboards.removeScoreboard(getPlayer());
        save();
        // lets fire an informal event
        RaidCraft.callEvent(new RCPlayerChangedProfessionEvent(getPlayer(), profession.getName(), profession.getAttachedLevel().getLevel()));
    }

    @Override
    public List<Skill> getSkills() {

        ArrayList<Skill> skills = new ArrayList<>(getVirtualProfession().getSkills());
        professions.values().stream()
                .filter(Profession::isActive)
                .filter(profession -> !profession.getName().equalsIgnoreCase(ProfessionManager.VIRTUAL_PROFESSION))
                .forEach(profession -> skills.addAll(profession.getSkills()));
        return skills;
    }

    @Override
    public Skill getSkill(String name) throws UnknownSkillException {

        List<Skill> foundSkills = new ArrayList<>();
        for (Skill skill : getSkills()) {
            if (skill.getName().equalsIgnoreCase(name)) {
                return skill;
            }
            if (skill.matches(name)) {
                foundSkills.add(skill);
            }
        }
        if (foundSkills.size() < 1) {
            throw new UnknownSkillException("Der Spieler hat keinen Skill mit dem Namen: " + name);
        }
        if (foundSkills.size() > 1) {
            throw new UnknownSkillException("Es gibt mehrere Skills mit dem Namen: " + name);
        }
        return foundSkills.get(0);
    }

    @Override
    public List<Profession> getProfessions() {

        return new ArrayList<>(professions.values());
    }

    @Override
    public void saveProfessions() {

        for (Profession profession : professions.values()) {
            if (profession.isActive()) {
                profession.save();
            }
        }
    }

    @Override
    public void saveSkills() {

        getSkills().forEach(Skill::save);
        getVirtualProfession().getSkills().forEach(Skill::save);
    }

    @Override
    public void save() {

        THero tHero = RaidCraft.getDatabase(SkillsPlugin.class).find(THero.class, getId());
        if (tHero == null) return;
        tHero.setPlayer(getName());
        tHero.setHealth(getHealth());
        tHero.setSelectedProfession(getSelectedProfession().getName());
        RaidCraft.getDatabase(SkillsPlugin.class).save(tHero);

        saveProfessions();
        saveLevelProgress(getAttachedLevel());
        getExpPool().saveLevelProgress();
        saveSkills();

        getOptions().set(Option.PVP, pvpEnabled);
        getOptions().save();
    }

    @Override
    public boolean hasSkill(Skill skill) {

        return hasSkill(skill.getName().toLowerCase());
    }

    @Override
    public boolean hasSkill(String name) {

        if (name == null || name.equals("")) return false;
        String id = name.toLowerCase();
        boolean hasSkill = getVirtualProfession().getSkills().stream()
                .filter(Skill::isUnlocked)
                .anyMatch(skill -> skill.getName().equalsIgnoreCase(id));
        if (getPlayer().isOnline()) {
            for (Profession profession : getProfessions()) {
                try {
                    if (profession.isActive() && profession.hasSkill(id)) {
                        Skill skill = getSkill(id);
                        hasSkill = skill.isActive() && skill.isEnabled() && skill.isUnlocked();
                        break;
                    }
                } catch (UnknownSkillException ignored) {
                }
            }
        } else {
            List<THeroSkill> skills = RaidCraft.getDatabase(SkillsPlugin.class).find(THero.class, getId()).getSkills();
            for (THeroSkill skill : skills) {
                if (skill.getName().equalsIgnoreCase(id)) {
                    hasSkill = true;
                    break;
                }
            }
        }
        return hasSkill;
    }

    @Override
    public boolean hasProfession(Profession profession) {

        return hasProfession(profession.getProperties().getName().toLowerCase());
    }

    @Override
    public boolean hasProfession(String id) {

        id = id.toLowerCase();
        return professions.containsKey(id);
    }

    @Override
    public Profession getProfession(String id) throws UnknownSkillException, UnknownProfessionException {

        id = id.toLowerCase();
        Profession profession;
        if (professions.containsKey(id)) {
            profession = professions.get(id);
        } else {
            profession = RaidCraft.getComponent(SkillsPlugin.class).getProfessionManager().getProfession(this, id);
            professions.put(id, profession);
        }
        return profession;
    }

    @Override
    public void sendMessage(String... messages) {

        if (isOnline()) {
            getPlayer().sendMessage(messages);
        }
    }

    @Override
    public void addSkill(Skill skill) {

        getVirtualProfession().addSkill(skill);
        save();
    }

    @Override
    public void removeSkill(Skill skill) {

        getVirtualProfession().removeSkill(skill);
        save();
    }

    @Override
    public void updatePermissions() {

        // permissions update is essentially reapplying the skills
        loadSkills();
    }

    public void updateHighestRankedProfession() {

        for (Profession profession : getProfessions()) {
            if (profession.isActive()) {
                if (highestRankedProfession == null) {
                    highestRankedProfession = profession;
                } else if (highestRankedProfession.getPath().getPriority() < profession.getPath().getPriority()) {
                    highestRankedProfession = profession;
                }
            }
        }
        if (highestRankedProfession == null) {
            highestRankedProfession = getVirtualProfession();
        }
    }

    private void addAttributes(Collection<ItemAttribute> attributes) {

        for (ItemAttribute attribute : attributes) {
            Attribute attr = getAttribute(attribute);
            if (attr != null) {
                attr.addValue(attribute.getValue());
            }
        }
    }
}