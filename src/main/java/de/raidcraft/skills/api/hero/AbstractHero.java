package de.raidcraft.skills.api.hero;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.items.ItemAttribute;
import de.raidcraft.skills.ProfessionManager;
import de.raidcraft.skills.Scoreboards;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.AbstractSkilledCharacter;
import de.raidcraft.skills.api.character.CharacterTemplate;
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
import de.raidcraft.skills.bindings.BindManager;
import de.raidcraft.skills.config.LevelConfig;
import de.raidcraft.skills.config.ProfessionConfig;
import de.raidcraft.skills.formulas.FormulaType;
import de.raidcraft.skills.logging.ExpLogger;
import de.raidcraft.skills.tables.THero;
import de.raidcraft.skills.tables.THeroSkill;
import de.raidcraft.skills.util.ConfigUtil;
import de.raidcraft.skills.util.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Silthus
 */
public abstract class AbstractHero extends AbstractSkilledCharacter<Hero> implements Hero {

    private final int id;
    private final AttachedLevel<Hero> expPool;
    private final HeroOptions options;
    private final UserInterface userInterface;
    private final Map<String, Skill> virtualSkills = new HashMap<>();
    private final Map<String, Profession> professions = new HashMap<>();
    private final Map<String, Resource> resources = new HashMap<>();
    private final Map<String, Attribute> attributes = new HashMap<>();
    private final Set<Path<Profession>> paths = new HashSet<>();
    private Party pendingPartyInvite;
    private Profession virtualProfession;
    // this just tells the client what to display in the experience bar and so on
    private Profession selectedProfession;
    private Profession highestRankedProfession;

    protected AbstractHero(Player player, HeroData data) {

        super(player);

        this.id = data.getId();
        this.expPool = new ExpPool(this, data.getExpPool());
        this.options = new HeroOptions(this);
        this.maxLevel = data.getMaxLevel();
        // level needs to be attached fast to avoid npes when loading the skills
        ConfigurationSection levelConfig = RaidCraft.getComponent(SkillsPlugin.class).getLevelConfig()
                .getConfigFor(LevelConfig.Type.HEROES, getName());
        FormulaType formulaType = FormulaType.fromName(levelConfig.getString("type", "wow"));
        attachLevel(new ConfigurableAttachedLevel<CharacterTemplate>(this, formulaType.create(levelConfig), data.getLevelData()));
        // load the professions first so we have the skills already loaded
        loadProfessions(data);
        loadAttributes();
        // keep this last because we need to professions to load first
        setMaxHealth(getDefaultHealth());
        setHealth(data.getHealth());
        // load the skills after the profession
        loadSkills();
        // it is important to load the user interface last or lese it will run in an endless loop
        this.userInterface = new BukkitUserInterface(this);
    }

    @Override
    public void updatePermissions() {

        // permissions update is essentially reapplying the skills
        loadSkills();
    }

    @SuppressWarnings("unchecked")
    private void loadProfessions(HeroData data) {

        ProfessionManager manager = RaidCraft.getComponent(SkillsPlugin.class).getProfessionManager();
        for (String professionName : data.getProfessionNames()) {
            try {
                Profession profession = manager.getProfession(this, professionName);
                professions.put(profession.getProperties().getName(), profession);
                paths.add(profession.getPath());
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
                RaidCraft.LOGGER.warning(e.getMessage());
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

    private void loadSkills() {

        virtualSkills.clear();
        // check all the profession skills for unlock
        for (Profession profession : professions.values()) {
            if (profession.isActive()) {
                // apply all skills that are already unlocked
                for (Skill skill : profession.getSkills()) {
                    if (skill.isUnlocked()) {
                        skill.apply();
                    }
                }
                profession.checkSkillsForUnlock();
            }
        }
        // make sure all virtual skills are added last and override normal skills
        for (Skill skill : getVirtualProfession().getSkills()) {
            if (skill.isUnlocked()) {
                skill.apply();
                virtualSkills.put(skill.getName(), skill);
            }
        }
        getVirtualProfession().checkSkillsForUnlock();
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

    @Override
    public void changeProfession(Profession profession) {

        // lets save once before we change it all to make sure the levels are safed
        save();
        // now lets check what we actually need to change
        // first lets check what path we are changing to and disable all professions on that path
        Path path = profession.getPath();
        for (Profession currentProf : getProfessions()) {
            if (currentProf.getPath().equals(path)) {
                currentProf.setActive(false);
                currentProf.save();
            }
        }
        // lets set the selected profession before we go all wanky in the while loop
        updateHighestRankedProfession();

        // now lets go thru all of the professions parents add them and activate them
        do {
            profession.setActive(true);
            professions.put(profession.getName(), profession);
            if (profession instanceof AbstractProfession) {
                ((AbstractProfession) profession).loadSkills();
                ((AbstractProfession) profession).loadResources();
            }
            profession.save();
            profession = profession.getParent();
        } while (profession != null);

        // lets clear all skills from the list and add them again for the profession
        loadSkills();
        loadAttributes();
        // update the display stuff
        updateHighestRankedProfession();
        updateSelectedProfession();
        // reload the bound items
        RaidCraft.getComponent(BindManager.class).reloadBoundItems(getPlayer());
        clearWeapons();
        Scoreboards.removeScoreboard(getPlayer());
        reset();
        save();
    }

    @Override
    public HeroOptions getOptions() {

        return options;
    }

    @Override
    public void addSkill(Skill skill) {

        getVirtualProfession().addSkill(skill);
    }

    @Override
    public void removeSkill(Skill skill) {

        getVirtualProfession().removeSkill(skill);
    }

    @Override
    public Skill getSkill(String name) throws UnknownSkillException {

        List<Skill> foundSkills = new ArrayList<>();
        for (Skill skill : getSkills()) {
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
    public int getAttributeValue(String attribute) {

        return getAttribute(attribute).getCurrentValue();
    }

    @Override
    public void setAttributeValue(String attribute, int value) {

        getAttribute(attribute).setCurrentValue(value);
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
    public Resource getResource(String name) {

        return resources.get(name);
    }

    @Override
    public boolean hasResource(String name) {

        return resources.containsKey(name.toLowerCase());
    }

    @Override
    public void attachResource(Resource resource) {

        if (resource.getProfession().isActive()) {
            resource.setEnabled(true);
            resources.put(resource.getName().toLowerCase(), resource);
        }
    }

    @Override
    public Resource detachResource(String name) {

        Resource resource = resources.remove(name.toLowerCase());
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
    public boolean hasPath(Path path) {

        return getPaths().contains(path);
    }

    @Override
    public Set<Path<Profession>> getPaths() {

        return paths;
    }

    @Override
    public void reset() {

        if (!isOnline()) {
            return;
        }
        updateHighestRankedProfession();
        updateSelectedProfession();
        setMaxHealth(getDefaultHealth());
        setHealth(getMaxHealth());
        clearEffects();
        for (Resource resource : getResources()) {
            resource.setCurrent(resource.getMax());
        }
        getUserInterface().refresh();
        debug("Reseted all active stats to max");
    }


    @Override
    public int getId() {

        return id;
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
    public void debug(String message) {

        if (Option.DEBUGGING.getBoolean(this) && message != null && !message.equals("")) {
            getPlayer().sendMessage(ChatColor.GRAY + "[DEBUG] " + ChatColor.ITALIC + message);
        }
    }

    @Override
    public void combatLog(String message) {

        combatLog(null, message);
    }

    @Override
    public void combatLog(Object o, String message) {

        if (Option.COMBAT_LOGGING.getBoolean(this) && message != null && !message.equals("")) {
            getPlayer().sendMessage(ChatColor.DARK_GRAY + "[Combat]" + (o != null ? "[" + o + "]" : "")
                    + " " + ChatColor.ITALIC + message);
        }
    }

    @Override
    public int getDamage() {

        int damage = super.getDamage();
        for (Attribute attribute : getAttributes()) {
            damage += attribute.getCurrentValue() * attribute.getDamageModifier();
        }
        return damage;
    }

    @Override
    public int getDefaultHealth() {

        Profession profession = getHighestRankedProfession();
        int health = 20;
        if (profession != null) {
            health = (int) (profession.getProperties().getBaseHealth()
                    + profession.getProperties().getBaseHealthModifier() * profession.getAttachedLevel().getLevel());
        }
        for (Attribute attribute : getAttributes()) {
            if (attribute.getHealthModifier() > 0.0) {
                health += attribute.getCurrentValue() * attribute.getHealthModifier();
            }
        }
        return health;
    }

    @Override
    public void setHealth(int health) {

        super.setHealth(health);
        if (getUserInterface() != null) {
            getUserInterface().refresh();
        }
    }

    @Override
    public void onExpGain(int exp) {

        ExpLogger.log(this, exp);
    }

    @Override
    public void onExpLoss(int exp) {

        ExpLogger.log(this, -exp);
    }

    @Override
    public void onLevelGain() {

        sendMessage(ChatColor.GREEN + "Du bist ein Level aufgestiegen: " +
                ChatColor.ITALIC + ChatColor.YELLOW + " Level " + getAttachedLevel().getLevel());
    }

    @Override
    public void onLevelLoss() {

        sendMessage(ChatColor.RED + "Du bist ein Level abgestiegen: " +
                ChatColor.ITALIC + ChatColor.YELLOW + " Level " + getAttachedLevel().getLevel());
    }

    @Override
    public void save() {

        THero tHero = RaidCraft.getDatabase(SkillsPlugin.class).find(THero.class, getId());
        if (tHero == null) return;
        tHero.setHealth(getHealth());
        tHero.setSelectedProfession(getSelectedProfession().getName());

        // dont save when the player is in a blacklist world
        if (isOnline() && RaidCraft.getComponent(SkillsPlugin.class).isSavingWorld(getPlayer().getWorld().getName())) {
            RaidCraft.getDatabase(SkillsPlugin.class).save(tHero);
            saveProfessions();
            saveLevelProgress(getAttachedLevel());
            getExpPool().saveLevelProgress();
            saveSkills();
        }
        getOptions().save();
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
    public void saveLevelProgress(AttachedLevel<CharacterTemplate> attachedLevel) {

        THero heroTable = RaidCraft.getDatabase(SkillsPlugin.class).find(THero.class, getId());
        heroTable.setExp(getAttachedLevel().getExp());
        heroTable.setLevel(getAttachedLevel().getLevel());

        // dont save when the player is in a blacklist world
        if (RaidCraft.getComponent(SkillsPlugin.class).isSavingWorld(getPlayer().getWorld().getName())) {
            RaidCraft.getDatabase(SkillsPlugin.class).save(heroTable);
        }
    }

    @Override
    public void saveSkills() {

        for (Skill skill : getSkills()) {
            skill.save();
        }
        for (Skill skill : getVirtualProfession().getSkills()) {
            skill.save();
        }
    }

    @Override
    public boolean hasSkill(String id) {

        id = id.toLowerCase();
        boolean hasSkill = virtualSkills.containsKey(id);
        if (getPlayer().isOnline()) {
            for (Profession profession : getProfessions()) {
                if (profession.isActive() && profession.hasSkill(id)) {
                    hasSkill = true;
                    break;
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
    public boolean hasSkill(Skill skill) {

        return hasSkill(skill.getName().toLowerCase());
    }

    @Override
    public List<Skill> getSkills() {

        ArrayList<Skill> skills = new ArrayList<>(virtualSkills.values());
        for (Profession profession : professions.values()) {
            if (!profession.getName().equalsIgnoreCase(ProfessionManager.VIRTUAL_PROFESSION) && profession.isActive()) {
                skills.addAll(profession.getSkills());
            }
        }
        return skills;
    }

    @Override
    public List<Profession> getProfessions() {

        return new ArrayList<>(professions.values());
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
    public void setInCombat(boolean inCombat) {

        if (inCombat != isInCombat()) {
            super.setInCombat(inCombat);
            updateSelectedProfession();
        }
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

    @Override
    public Profession getVirtualProfession() {

        return virtualProfession;
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
    public boolean hasProfession(String id) {

        id = id.toLowerCase();
        return professions.containsKey(id);
    }

    @Override
    public boolean hasProfession(Profession profession) {

        return hasProfession(profession.getProperties().getName().toLowerCase());
    }

    @Override
    public void kill(CharacterTemplate attacker) {

        super.kill(attacker);
        debug(attacker.getName() + " killed YOU");
    }

    @Override
    public void sendMessage(String... messages) {

        if (isOnline()) {
            getPlayer().sendMessage(messages);
        }
    }

    @Override
    public boolean equals(Object obj) {

        return obj instanceof Hero
                && ((Hero) obj).getName().equals(getName());
    }
}
