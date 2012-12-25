package de.raidcraft.skills.api.hero;

import com.avaje.ebean.Ebean;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.InvalidTargetException;
import de.raidcraft.api.database.Database;
import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.skills.ProfessionManager;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.AbstractCharacterTemplate;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.exceptions.InvalidChoiceException;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.level.Level;
import de.raidcraft.skills.api.level.Levelable;
import de.raidcraft.skills.api.persistance.Equipment;
import de.raidcraft.skills.api.persistance.HeroData;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.tables.THero;
import de.raidcraft.skills.tables.THeroSkill;
import de.raidcraft.util.BukkitUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.*;

/**
 * @author Silthus
 */
public abstract class AbstractHero extends AbstractCharacterTemplate implements Hero {

    private static final String META_DATA_KEY = "rcs_selected_prof";

    private final int id;
    private final RCPlayer player;
    private boolean debugging = false;
    private int health;
    private int mana;
    private int stamina;
    private int maxLevel;
    private final Map<String, Skill> skills = new HashMap<>();
    private final Map<String, Profession> professions = new HashMap<>();
    private Set<Equipment> equipment = new HashSet<>();
    // primary and secondary professions are the ones defining items and stuff
    private Profession primaryProfession;
    private Profession secundaryProfession;
    // this just tells the client what to display in the experience bar and so on
    private Profession selectedProfession;

    protected AbstractHero(HeroData data) {

        super(Bukkit.getPlayer(data.getName()));

        this.id = data.getId();
        this.player = RaidCraft.getPlayer(data.getName());
        setHealth(data.getHealth());
        this.maxLevel = data.getMaxLevel();
        // load the professions first so we have the skills already loaded
        loadProfessions(data.getProfessionNames());
        loadSkills();

        this.selectedProfession = getSelectedProfession();

        // add equipment from the primary and secundary profession
        // we need to make sure to add the secundary equipment first because it is overriden by the primary class
        if (getSecundaryProfession() != null) {
            equipment.addAll(getSecundaryProfession().getProperties().getEquipment());
        }
        if (getPrimaryProfession() != null) {
            equipment.addAll(getPrimaryProfession().getProperties().getEquipment());
        }
    }

    private void loadProfessions(List<String> professionNames) {

        ProfessionManager manager = RaidCraft.getComponent(SkillsPlugin.class).getProfessionManager();
        for (String professionName : professionNames) {
            try {
                Profession profession = manager.getProfession(this, professionName);
                professions.put(profession.getProperties().getName(), profession);
                // set the primary and secundary profession
                if (profession.isActive()) {
                    if (profession.getProperties().isPrimary()) {
                        primaryProfession = profession;
                    } else {
                        secundaryProfession = profession;
                    }
                }
            } catch (UnknownSkillException | UnknownProfessionException e) {
                RaidCraft.LOGGER.warning(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void loadSkills() {

        // this simple creates a second reference to all skills owned by the player
        // to allow faster access to the player skills
        for (Profession profession : professions.values()) {
            for (Skill skill : profession.getSkills()) {
                // unlock skills if needed
                if (!skill.isUnlocked() && !(skill.getProperties().getRequiredLevel() < skill.getProfession().getLevel().getLevel())) {
                    skill.unlock();
                }
                // only add active skills
                if (skill.isActive()) {
                    skills.put(skill.getName(), skill);
                }
            }
        }
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
        professions.put(profession.getProperties().getName(), profession);
        setSelectedProfession(profession);
        // lets clear all skills from the list and add them again for the profession
        skills.clear();
        // readd all skills and unlock if needed
        ArrayList<Skill> skills = new ArrayList<>(profession.getSkills());
        // and readd the skills from the second prof
        if (profession.getProperties().isPrimary()) {
            if (secundaryProfession != null) skills.addAll(secundaryProfession.getSkills());
        } else {
            if (primaryProfession != null) skills.addAll(primaryProfession.getSkills());
        }
        for (Skill skill : skills) {
            if (skill.isActive()) {
                this.skills.put(skill.getName(), skill);
            }
        }
        // reset the current progress and save
        reset();
        save();
    }

    @Override
    public void reset() {

        if (getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        // TODO(BUG): does not seem to be called
        //TODO: add more reset stuff
        setHealth(getMaxHealth());
        setStamina(getMaxStamina());
        setMana(getMaxMana());
        setInCombat(false);
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
    public boolean isDebugging() {

        return debugging;
    }

    @Override
    public void setDebugging(boolean debug) {

        this.debugging = debug;
    }

    @Override
    public void debug(String message) {

        if (isDebugging() && message != null && !message.equals("")) {
            player.sendMessage(ChatColor.GRAY + "[DEBUG] " + ChatColor.ITALIC + message);
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
    public boolean canChooseProfession(Profession profession) throws InvalidChoiceException {

        if (profession.getStrongParents().size() > 0) {
            for (Profession strongParent : profession.getStrongParents()) {
                if (!strongParent.isMastered()) {
                    throw new InvalidChoiceException("Du musst erst alle " +
                            (profession.getProperties().isPrimary() ? "Klassen die diese Klasse" : "Berufe die dieser Beruf")
                            + " benötigt meistern.");
                }
            }
        }
        if (profession.getWeakParents().size() > 0) {
            boolean oneMastered = false;
            for (Profession weakParent : profession.getWeakParents()) {
                if (weakParent.isMastered()) {
                    oneMastered = true;
                    break;
                }
            }
            if (!oneMastered) {
                throw new InvalidChoiceException("Du musst erst mindestens " +
                        (profession.getProperties().isPrimary() ? "eine Klasse die diese Klasse" : "einen Beruf der diesen Beruf")
                        + " benötigt meistern.");
            }
        }
        return true;
    }

    @Override
    public int getDamage() {

        ItemStack itemInHand = getPlayer().getItemInHand();
        for (Equipment equipment : this.equipment) {
            if (equipment.equals(itemInHand)) {
                return (int) (equipment.getBaseDamage()
                                        + (equipment.getDamageLevelModifier() * getLevel().getLevel())
                                        + (equipment.getDamageProfessionLevelModifier() * getPrimaryProfession().getLevel().getLevel()));
            }
        }
        return 0;
    }

    @Override
    public int getHealth() {

        return health;
    }

    @Override
    public void setHealth(int health) {

        this.health = health;
        if (getUserInterface() != null) {
            getUserInterface().refresh();
        }
        debug("Health set to " + health);
    }

    @Override
    public int getMaxHealth() {

        Profession profession;
        if (getPrimaryProfession() != null ) {
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
    public int getMana() {

        return mana;
    }

    @Override
    public void setMana(int mana) {

        this.mana = mana;
    }

    @Override
    public int getMaxMana() {

        Profession profession = getPrimaryProfession();
        if (profession == null) return 100;
        return (int) (profession.getProperties().getBaseMana()
                + profession.getProperties().getBaseManaModifier() * profession.getLevel().getLevel());
    }

    @Override
    public int getStamina() {

        return stamina;
    }

    @Override
    public void setStamina(int stamina) {

        this.stamina = stamina;
    }

    @Override
    public int getMaxStamina() {

        Profession profession = getPrimaryProfession();
        if (profession == null) return 20;
        return (int) (profession.getProperties().getBaseStamina() +
                        profession.getProperties().getBaseStaminaModifier() * profession.getLevel().getLevel());
    }

    @Override
    public void save() {

        THero database = Ebean.find(THero.class, getId());
        database.setHealth(getHealth());
        Ebean.save(database);
        saveProfessions();
        saveLevelProgress(getLevel());
        saveSkills();
    }

    @Override
    public void saveProfessions() {

        for (Profession profession : professions.values()) {
            profession.getLevel().saveLevelProgress();
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
            if (skill instanceof Levelable) {
                ((Levelable) skill).getLevel().saveLevelProgress();
            }
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

    public Skill getSkill(String id) throws UnknownSkillException {

        id = id.toLowerCase();
        if (!hasSkill(id)) {
            throw new UnknownSkillException("Der Spieler hat keinen Skill mit der ID: " + id);
        }
        if (!skills.containsKey(id)) {
            Skill skill = RaidCraft.getComponent(SkillsPlugin.class).getSkillManager().getSkill(this, id);
            skills.put(id, skill);
        }
        return skills.get(id);
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
            SkillsPlugin plugin = RaidCraft.getComponent(SkillsPlugin.class);

            // lets get the selected prof via the metadata values
            if (professions.size() > 0) {
                for (MetadataValue value : getEntity().getMetadata(META_DATA_KEY)) {
                    if (value.getOwningPlugin().equals(plugin)) {
                        try {
                            this.selectedProfession = plugin.getProfessionManager().getProfession(this, value.asString());
                        } catch (UnknownSkillException | UnknownProfessionException e) {
                            e.printStackTrace();
                            plugin.getLogger().warning(e.getMessage());
                        }
                    }
                }
            }
            // if the metadata returned null choose the primary or secondary prof
            if (this.selectedProfession == null) {
                if (getPrimaryProfession() != null) {
                    setSelectedProfession(getPrimaryProfession());
                } else if (getSecundaryProfession() != null) {
                    setSelectedProfession(getSecundaryProfession());
                }
            }
        }
        return selectedProfession;
    }

    @Override
    public void setSelectedProfession(Profession profession) {

        this.selectedProfession = profession;
        // lets set the metadata
        SkillsPlugin plugin = RaidCraft.getComponent(SkillsPlugin.class);
        getEntity().setMetadata(META_DATA_KEY, new FixedMetadataValue(plugin, profession.getName()));
        if (getUserInterface() != null) {
            getUserInterface().refresh();
        }
    }

    @Override
    public Set<Equipment> getEquipment() {

        return equipment;
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
    public CharacterTemplate getTarget() throws InvalidTargetException {

        LivingEntity target = BukkitUtil.getTargetEntity(getEntity(), LivingEntity.class);
        if (target == null) {
            throw new InvalidTargetException("Du hast kein Ziel anvisiert!");
        }
        return RaidCraft.getComponent(SkillsPlugin.class).getCharacterManager()
                .getCharacter(target);
    }

    @Override
    public Location getBlockTarget() {

        return getEntity().getTargetBlock(null, 100).getLocation();
    }

    @Override
    public void sendMessage(String... messages) {

        player.sendMessage(messages);
    }

    @Override
    public boolean equals(Object obj) {

        return obj instanceof Hero && ((Hero) obj).getName().equalsIgnoreCase(getName());
    }

    @Override
    public String toString() {

        return "[H:" + getName() + "]";
    }
}
