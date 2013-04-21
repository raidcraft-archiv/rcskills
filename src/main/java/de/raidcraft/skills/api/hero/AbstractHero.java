package de.raidcraft.skills.api.hero;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.ProfessionManager;
import de.raidcraft.skills.Scoreboards;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.AbstractCharacterTemplate;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.level.AttachedLevel;
import de.raidcraft.skills.api.level.ConfigurableAttachedLevel;
import de.raidcraft.skills.api.level.ExpPool;
import de.raidcraft.skills.api.path.Path;
import de.raidcraft.skills.api.persistance.HeroData;
import de.raidcraft.skills.api.profession.AbstractProfession;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.resource.Resource;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.ui.BukkitUserInterface;
import de.raidcraft.skills.api.ui.UserInterface;
import de.raidcraft.skills.config.LevelConfig;
import de.raidcraft.skills.formulas.FormulaType;
import de.raidcraft.skills.logging.ExpLogger;
import de.raidcraft.skills.tables.THero;
import de.raidcraft.skills.tables.THeroSkill;
import de.raidcraft.skills.util.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
public abstract class AbstractHero extends AbstractCharacterTemplate implements Hero {

    private final int id;
    private final AttachedLevel<Hero> expPool;
    private final HeroOptions options;
    private final UserInterface userInterface;
    private final Map<String, Skill> virtualSkills = new HashMap<>();
    private final Map<String, Profession> professions = new HashMap<>();
    private final Map<String, Resource> resources = new HashMap<>();
    private final Map<String, Attribute> attributes = new HashMap<>();
    private final Set<Path<Profession>> paths = new HashSet<>();
    private int maxLevel;
    private AttachedLevel<Hero> attachedLevel;
    private Profession virtualProfession;
    // this just tells the client what to display in the experience bar and so on
    private Profession selectedProfession;

    protected AbstractHero(Player player, HeroData data) {

        super(player);

        this.id = data.getId();
        this.expPool = new ExpPool(this, data.getExpPool());
        this.options = new HeroOptions(this);
        this.userInterface = new BukkitUserInterface(this);
        this.maxLevel = data.getMaxLevel();
        // level needs to be attached fast to avoid npes when loading the skills
        ConfigurationSection levelConfig = RaidCraft.getComponent(SkillsPlugin.class).getLevelConfig()
                .getConfigFor(LevelConfig.Type.HEROES, getName());
        FormulaType formulaType = FormulaType.fromName(levelConfig.getString("type", "wow"));
        attachLevel(new ConfigurableAttachedLevel<Hero>(this, formulaType.create(levelConfig), data.getLevelData()));
        // load the professions first so we have the skills already loaded
        loadProfessions(data);
        loadSkills();
        // keep this last because we need to professions to load first
        setMaxHealth(getDefaultHealth());
        setHealth(data.getHealth());
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
                if (selectedProfession == null || getSelectedProfession().getPath().getPriority() <= profession.getPath().getPriority()
                        && profession.isActive()) {
                    setSelectedProfession(profession);
                }
                // also add the parent if one exists
                if (profession.getParent() != null) {
                    professions.put(profession.getParent().getName(), profession.getParent());
                }
                // add all child professions to our list
                for (Profession child : profession.getChildren()) {
                    professions.put(child.getName(), child);
                }
                if (profession instanceof AbstractProfession) {
                    ((AbstractProfession) profession).loadSkills();
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
                profession.checkSkillsForUnlock();
            }
        }
        getVirtualProfession().checkSkillsForUnlock();
        // make sure all virtual skills are added last and override normal skills
        for (Skill skill : getVirtualProfession().getSkills()) {
            if (skill.isUnlocked()) {
                virtualSkills.put(skill.getName(), skill);
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
        if (getSelectedProfession().getPath().getPriority() <= profession.getPath().getPriority()) {
            setSelectedProfession(profession);
        }

        // now lets go thru all of the professions parents add them and activate them
        do {
            profession.setActive(true);
            professions.put(profession.getName(), profession);
            if (profession instanceof AbstractProfession) {
                ((AbstractProfession) profession).loadSkills();
            }
            profession.save();
            profession = profession.getParent();
        } while (profession != null);

        // lets clear all skills from the list and add them again for the profession
        loadSkills();
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
        // we need to reload the skills in order for normal profession skills to load
        loadSkills();
    }

    @Override
    public void removeSkill(Skill skill) {

        getVirtualProfession().removeSkill(skill);
        // we need to reload the skills in order for normal profession skills to load
        loadSkills();
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
    public Material getItemTypeInHand() {

        ItemStack itemInHand = getPlayer().getInventory().getItemInHand();
        if (itemInHand == null || itemInHand.getTypeId() == 0) {
            return Material.AIR;
        }
        return itemInHand.getType();
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
    public int getAttributeValue(String attribute) {

        return getAttribute(attribute).getValue();
    }

    @Override
    public void setAttributeValue(String attribute, int value) {

        getAttribute(attribute).setValue(value);
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
    public Set<Path<Profession>> getPaths() {

        return paths;
    }

    @Override
    public void reset() {

        if (getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        Scoreboards.removeScoreboard(getPlayer());
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
    public AttachedLevel<Hero> getExpPool() {

        return expPool;
    }

    @Override
    public UserInterface getUserInterface() {

        return userInterface;
    }

    @Override
    public boolean isMastered() {

        return getAttachedLevel().hasReachedMaxLevel();
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
    public int getDefaultHealth() {

        return (int) (getSelectedProfession().getProperties().getBaseHealth()
                + getSelectedProfession().getProperties().getBaseHealthModifier() * getSelectedProfession().getAttachedLevel().getLevel());
    }

    @Override
    public AttachedLevel<Hero> getAttachedLevel() {

        return attachedLevel;
    }

    @Override
    public void attachLevel(AttachedLevel<Hero> attachedLevel) {

        this.attachedLevel = attachedLevel;
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
        if (RaidCraft.getComponent(SkillsPlugin.class).isSavingWorld(getPlayer().getWorld().getName())) {
            RaidCraft.getDatabase(SkillsPlugin.class).save(tHero);
        }

        getOptions().save();
        saveProfessions();
        saveLevelProgress(getAttachedLevel());
        getExpPool().saveLevelProgress();
        saveSkills();
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
    public void saveLevelProgress(AttachedLevel<Hero> attachedLevel) {

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

    @Override
    public Profession getSelectedProfession() {

        if (selectedProfession == null) {
            setSelectedProfession(getVirtualProfession());
        }
        return selectedProfession;
    }

    @Override
    public void setSelectedProfession(Profession profession) {

        this.selectedProfession = profession;
        setMaxHealth(getDefaultHealth());
        getUserInterface().refresh();
    }

    @Override
    public Profession getVirtualProfession() {

        return virtualProfession;
    }

    @Override
    public int getMaxLevel() {

        return maxLevel;
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

        getPlayer().sendMessage(messages);
    }

    @Override
    public boolean equals(Object obj) {

        return obj instanceof Hero
                && ((Hero) obj).getName().equals(getName());
    }
}
