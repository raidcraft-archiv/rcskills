package de.raidcraft.skills.api.hero;

import com.avaje.ebean.Ebean;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.database.Database;
import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.skills.ProfessionManager;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.AbstractCharacterTemplate;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.group.Group;
import de.raidcraft.skills.api.group.SimpleGroup;
import de.raidcraft.skills.api.level.ExpPool;
import de.raidcraft.skills.api.level.Level;
import de.raidcraft.skills.api.persistance.HeroData;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.resource.Resource;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.hero.HeroLevel;
import de.raidcraft.skills.tables.THero;
import de.raidcraft.skills.tables.THeroSkill;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
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
    // every player is member of his own group by default
    private Group group;
    private boolean debugging = false;
    private boolean combatLoggging = false;
    private int health;
    private int maxHealth = 20;
    private int maxLevel;
    private final Map<String, Skill> skills = new HashMap<>();
    private final Map<String, Profession> professions = new HashMap<>();
    private Level<Hero> level;
    // primary and secondary professions are the ones defining items and stuff
    private Profession primaryProfession;
    private Profession secundaryProfession;
    private Profession virtualProfession;
    // this just tells the client what to display in the experience bar and so on
    private Profession selectedProfession;

    protected AbstractHero(HeroData data) {

        super(Bukkit.getPlayer(data.getName()));

        this.id = data.getId();
        this.player = RaidCraft.getPlayer(data.getName());
        this.expPool = new ExpPool(this, data.getExpPool());
        this.health = data.getHealth();
        this.debugging = data.isDebugging();
        this.combatLoggging = data.isCombatLogging();
        this.maxLevel = data.getMaxLevel();
        this.selectedProfession = loadSelectedProfession(data);
        // level needs to be attached fast to avoid npes when loading the skills
        attachLevel(new HeroLevel(this, data.getLevelData()));
        // load the professions first so we have the skills already loaded
        loadProfessions(data.getProfessionNames());
        loadSkills();

        this.virtualProfession = getVirtualProfession();
        this.selectedProfession = getSelectedProfession();
        this.group = new SimpleGroup(this);
    }

    private void loadProfessions(List<String> professionNames) {

        ProfessionManager manager = RaidCraft.getComponent(SkillsPlugin.class).getProfessionManager();
        for (String professionName : professionNames) {
            try {
                Profession profession = manager.getProfession(this, professionName);
                if (profession.getName().equals(ProfessionManager.VIRTUAL_PROFESSION)) {
                    this.virtualProfession = profession;
                } else {
                    professions.put(profession.getProperties().getName(), profession);
                    // set the primary and secundary profession
                    if (profession.isActive()) {
                        if (profession.getProperties().isPrimary()) {
                            primaryProfession = profession;
                        } else {
                            secundaryProfession = profession;
                        }
                    }
                }
            } catch (UnknownSkillException | UnknownProfessionException e) {
                RaidCraft.LOGGER.warning(e.getMessage());
            }
        }
    }

    private void loadSkills() {

        skills.clear();
        // this simple creates a second reference to all skills owned by the player
        // to allow faster access to the player skills
        for (Profession profession : professions.values()) {
            if (profession.getName().equals(ProfessionManager.VIRTUAL_PROFESSION)) {
                continue;
            }
            profession.checkSkillsForUnlock();
            for (Skill skill : profession.getSkills()) {
                // only add active skills
                if (skill.isActive()) {
                    skills.put(skill.getName(), skill);
                }
            }
        }
        // make sure all virtual skills are added first so they are overriden by gained normal prof skills
        for (Skill skill : getVirtualProfession().getSkills()) {
            if (skill.isUnlocked()) {
                skills.put(skill.getName(), skill);
            }
        }
    }

    private Profession loadSelectedProfession(HeroData data) {

        if (data.getSelectedProfession() != null && !data.getSelectedProfession().equals("")) {
            try {
                return RaidCraft.getComponent(SkillsPlugin.class).getProfessionManager().getProfession(this, data.getSelectedProfession());
            } catch (UnknownSkillException | UnknownProfessionException ignored) {
                // do nothing and return the getter
            }
        }
        return getSelectedProfession();
    }

    @Override
    public void changeProfession(Profession profession) {

        // lets save once before we change it all to make sure the levels are safed
        save();
        // now lets check what we actually need to change
        if (profession.getProperties().isPrimary()) {
            if (primaryProfession != null) primaryProfession.setActive(false);
            primaryProfession = profession;
        } else {
            if (secundaryProfession != null) secundaryProfession.setActive(false);
            secundaryProfession = profession;
        }
        profession.setActive(true);
        professions.put(profession.getName(), profession);
        setSelectedProfession(profession);
        // lets clear all skills from the list and add them again for the profession
        loadSkills();
        // reset the current progress and save
        reset();
        save();
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

        Resource resource = primaryProfession.getResource(type);
        if (resource == null) resource = secundaryProfession.getResource(type);
        return resource;
    }

    @Override
    public Set<Resource> getResources() {

        HashSet<Resource> resources = new HashSet<>();
        if (primaryProfession != null) resources.addAll(primaryProfession.getResources());
        if (secundaryProfession != null) resources.addAll(secundaryProfession.getResources());
        return resources;
    }

    @Override
    public Set<Resource> getResources(Profession profession) {

        return profession.getResources();
    }

    @Override
    public void reset() {

        if (getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
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
    public Level<Hero> getExpPool() {

        return expPool;
    }

    @Override
    public boolean isDebugging() {

        return debugging;
    }

    @Override
    public void setDebugging(boolean debug) {

        this.debugging = debug;
    }

    @Override
    public boolean isCombatLogging() {

        return combatLoggging;
    }

    @Override
    public void setCombatLogging(boolean logging) {

        this.combatLoggging = logging;
    }

    @Override
    public boolean isMastered() {

        return getLevel().hasReachedMaxLevel();
    }

    @Override
    public void debug(String message) {

        if (isDebugging() && message != null && !message.equals("")) {
            player.sendMessage(ChatColor.GRAY + "[DEBUG] " + ChatColor.ITALIC + message);
        }
    }

    @Override
    public void combatLog(String message) {

        if (isCombatLogging() && message != null && !message.equals("")) {
            player.sendMessage(ChatColor.GRAY + "[Combat]" + ChatColor.ITALIC + message);
        }
    }

    @Override
    public Profession getPrimaryProfession() {

        return primaryProfession;
    }

    @Override
    public Profession getSecundaryProfession() {

        return secundaryProfession;
    }

    @Override
    public int getMaxHealth() {

        return getDefaultHealth();
    }

    @Override
    public int getHealth() {

        return health;
    }

    @Override
    public void setHealth(int health) {

        if (health > getMaxHealth()) health = getMaxHealth();
        this.health = health;
        debug("Health set to " + health);
    }

    @Override
    public int getDefaultHealth() {

        Profession profession;
        if (getPrimaryProfession() != null) {
            profession = getPrimaryProfession();
        } else if (getSecundaryProfession() != null) {
            profession = getSecundaryProfession();
        } else {
            return getEntity().getMaxHealth();
        }
        return (int) (profession.getProperties().getBaseHealth()
                + profession.getProperties().getBaseHealthModifier() * profession.getLevel().getLevel());
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
        tHero.setDebugging(isDebugging());
        tHero.setCombatLogging(isCombatLogging());
        tHero.setSelectedProfession(getSelectedProfession().getName());
        Database.save(tHero);

        saveProfessions();
        saveLevelProgress(getLevel());
        getExpPool().saveLevelProgress();
        saveSkills();
    }

    @Override
    public void saveProfessions() {

        for (Profession profession : professions.values()) {
            profession.save();
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
        if (player.isOnline()) {
            return skills.containsKey(id);
        } else {
            List<THeroSkill> skills = Ebean.find(THero.class, getId()).getSkills();
            for (THeroSkill skill : skills) {
                if (skill.getName().equalsIgnoreCase(id)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean hasSkill(Skill skill) {

        return hasSkill(skill.getName().toLowerCase());
    }

    @Override
    public List<Skill> getSkills() {

        return new ArrayList<>(skills.values());
    }

    @Override
    public List<Profession> getProfessions() {

        return new ArrayList<>(professions.values());
    }

    @Override
    public Profession getSelectedProfession() {

        if (selectedProfession == null) {
            // if the metadata returned null choose the primary or secondary prof
            if (getPrimaryProfession() != null) {
                setSelectedProfession(getPrimaryProfession());
            } else if (getSecundaryProfession() != null) {
                setSelectedProfession(getSecundaryProfession());
            } else {
                setSelectedProfession(getVirtualProfession());
            }
        }
        return selectedProfession;
    }

    @Override
    public void setSelectedProfession(Profession profession) {

        this.selectedProfession = profession;
        if (getUserInterface() != null) {
            getUserInterface().refresh();
        }
    }

    @Override
    public Profession getVirtualProfession() {

        if (virtualProfession == null) {
            return RaidCraft.getComponent(SkillsPlugin.class).getProfessionManager().getVirtualProfession(this);
        }
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

    @Override
    public String toString() {

        return "[H:" + getName() + "]";
    }
}
