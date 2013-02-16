package de.raidcraft.skills.api.hero;

import com.avaje.ebean.Ebean;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.database.Database;
import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.skills.ProfessionManager;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.AbstractCharacterTemplate;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.action.Attack;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.group.Group;
import de.raidcraft.skills.api.group.SimpleGroup;
import de.raidcraft.skills.api.level.ConfigurableLevel;
import de.raidcraft.skills.api.level.ExpPool;
import de.raidcraft.skills.api.level.Level;
import de.raidcraft.skills.formulas.FormulaType;
import de.raidcraft.skills.api.path.Path;
import de.raidcraft.skills.api.persistance.HeroData;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.resource.Resource;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.config.LevelConfig;
import de.raidcraft.skills.tables.THero;
import de.raidcraft.skills.tables.THeroSkill;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
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
    private final RCPlayer player;
    private final Level<Hero> expPool;
    private final HeroOptions options;
    // every player is member of his own group by default
    private Group group;
    private int health;
    private int maxHealth = 20;
    private int maxLevel;
    private final Map<String, Skill> virtualSkills = new HashMap<>();
    private final Map<String, Profession> professions = new HashMap<>();
    private final Set<Path<Profession>> paths = new HashSet<>();
    private Level<Hero> level;
    private Profession virtualProfession;
    // this just tells the client what to display in the experience bar and so on
    private Profession selectedProfession;

    protected AbstractHero(HeroData data) {

        super(data.getName());

        this.id = data.getId();
        this.player = RaidCraft.getPlayer(data.getName());
        this.expPool = new ExpPool(this, data.getExpPool());
        this.options = new HeroOptions(this);
        this.health = data.getHealth();
        this.maxLevel = data.getMaxLevel();
        // level needs to be attached fast to avoid npes when loading the skills
        ConfigurationSection levelConfig = RaidCraft.getComponent(SkillsPlugin.class).getLevelConfig()
                .getConfigFor(LevelConfig.Type.HEROES, getName());
        FormulaType formulaType = FormulaType.fromName(levelConfig.getString("type", "wow"));
        attachLevel(new ConfigurableLevel<Hero>(this, formulaType.create(levelConfig), data.getLevelData()));
        // load the professions first so we have the skills already loaded
        loadProfessions(data.getProfessionNames());
        loadSkills();

        this.group = new SimpleGroup(this);
    }

    @SuppressWarnings("unchecked")
    private void loadProfessions(List<String> professionNames) {

        ProfessionManager manager = RaidCraft.getComponent(SkillsPlugin.class).getProfessionManager();
        for (String professionName : professionNames) {
            try {
                Profession profession = manager.getProfession(this, professionName);
                if (profession.getName().equals(ProfessionManager.VIRTUAL_PROFESSION)) {
                    this.virtualProfession = profession;
                } else {
                    professions.put(profession.getProperties().getName(), profession);
                    paths.add(profession.getPath());
                    // set selected profession
                    if (getSelectedProfession().getPath().getPriority() <= profession.getPath().getPriority()
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
                }
            } catch (UnknownSkillException | UnknownProfessionException e) {
                RaidCraft.LOGGER.warning(e.getMessage());
            }
        }
        if (virtualProfession == null) {
            this.virtualProfession = manager.getVirtualProfession(this);
        }
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
                if (profession.isActive()) {
                    currentProf.setActive(false);
                    profession.save();
                }
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
    public void damage(Attack attack) {

        super.damage(attack);
        getUserInterface().refresh();
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
    public Group getGroup() {

        return group;
    }

    @Override
    public boolean isInGroup(Group group) {

        return group.isInGroup(this);
    }

    @Override
    public void joinGroup(Group group) {

        if (!this.group.equals(group)) {
            this.group = group;
            group.addMember(this);
        }
    }

    @Override
    public void leaveGroup(Group group) {

        if (this.group.equals(group)) {
            this.group = new SimpleGroup(this);
            group.removeMember(this);
        }
    }

    @Override
    public boolean isFriendly(Hero source) {

        return getGroup().isInGroup(source);
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
    public Resource getResource(String type) {

        for (Profession profession : professions.values()) {
            if (profession.isActive() && profession.getResource(type) != null) {
                return profession.getResource(type);
            }
        }
        return null;
    }

    @Override
    public Set<Resource> getResources() {

        Set<Resource> resources = new HashSet<>();
        for (Profession profession : professions.values()) {
            if (profession.isActive()) {
                resources.addAll(profession.getResources());
            }
        }
        return resources;
    }

    @Override
    public Set<Resource> getResources(Profession profession) {

        return profession.getResources();
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
        setMaxHealth(getDefaultHealth());
        setHealth(getMaxHealth());
        clearEffects();
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
    public RCPlayer getRCPlayer() {

        return player;
    }

    @Override
    public Level<Hero> getExpPool() {

        return expPool;
    }

    @Override
    public boolean isMastered() {

        return getLevel().hasReachedMaxLevel();
    }

    @Override
    public void debug(String message) {

        if (Option.DEBUGGING.isSet(this) && message != null && !message.equals("")) {
            player.sendMessage(ChatColor.GRAY + "[DEBUG] " + ChatColor.ITALIC + message);
        }
    }

    @Override
    public void combatLog(String message) {

        combatLog(null, message);
    }

    @Override
    public void combatLog(Object o, String message) {

        if (Option.COMBAT_LOGGING.isSet(this) && message != null && !message.equals("")) {
            player.sendMessage(ChatColor.DARK_GRAY + "[Combat]" + (o != null ? "[" + o + "]" : "")
                    + " " + ChatColor.ITALIC + message);
        }
    }

    @Override
    public int getMaxHealth() {

        return maxHealth;
    }

    @Override
    public void setMaxHealth(int maxHealth) {

        this.maxHealth = maxHealth;
    }

    @Override
    public int getHealth() {

        return health;
    }

    @Override
    public void setHealth(int health) {

        if (health > getMaxHealth()) health = getMaxHealth();
        this.health = health;
        if (getUserInterface() != null) {
            getUserInterface().refresh();
        }
        debug("Health set to " + health);
    }

    @Override
    public int getDefaultHealth() {

        return (int) (getSelectedProfession().getProperties().getBaseHealth()
                + getSelectedProfession().getProperties().getBaseHealthModifier() * getSelectedProfession().getLevel().getLevel());
    }

    @Override
    public void kill() {

        setHealth(0);
        super.kill();
    }

    @Override
    public Level<Hero> getLevel() {

        return level;
    }

    @Override
    public void attachLevel(Level<Hero> level) {

        this.level = level;
    }

    @Override
    public void onExpGain(int exp) {

    }

    @Override
    public void onExpLoss(int exp) {

    }

    @Override
    public void onLevelGain() {

        sendMessage(ChatColor.GREEN + "Du bist ein Level aufgestiegen: " +
                ChatColor.ITALIC + ChatColor.YELLOW + " Level " + getLevel().getLevel());
    }

    @Override
    public void onLevelLoss() {

        sendMessage(ChatColor.RED + "Du bist ein Level abgestiegen: " +
                ChatColor.ITALIC + ChatColor.YELLOW + " Level " + getLevel().getLevel());
    }

    @Override
    public void save() {

        THero tHero = Ebean.find(THero.class, getId());
        tHero.setHealth(getHealth());
        tHero.setSelectedProfession(getSelectedProfession().getName());
        Database.save(tHero);

        getOptions().save();
        saveProfessions();
        saveLevelProgress(getLevel());
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
    public void saveLevelProgress(Level<Hero> level) {

        THero heroTable = Ebean.find(THero.class, getId());
        heroTable.setExp(getLevel().getExp());
        heroTable.setLevel(getLevel().getLevel());
        Database.save(heroTable);
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
        if (player.isOnline()) {
            for (Profession profession : getProfessions()) {
                if (profession.isActive() && profession.hasSkill(id)) {
                    hasSkill = true;
                    break;
                }
            }
        } else {
            List<THeroSkill> skills = Ebean.find(THero.class, getId()).getSkills();
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
            if (profession.isActive()) {
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
        if (getUserInterface() != null) {
            getUserInterface().refresh();
        }
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

        player.sendMessage(messages);
    }

    @Override
    public boolean equals(Object obj) {

        return obj instanceof Hero
                && ((Hero) obj).getName().equals(getName());
    }
}
