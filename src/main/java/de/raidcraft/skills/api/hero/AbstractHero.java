package de.raidcraft.skills.api.hero;

import com.avaje.ebean.Ebean;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.InvalidTargetException;
import de.raidcraft.api.database.Database;
import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.skills.ProfessionManager;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.AreaAttack;
import de.raidcraft.skills.api.Passive;
import de.raidcraft.skills.api.TargetedAttack;
import de.raidcraft.skills.api.character.AbstractCharacterTemplate;
import de.raidcraft.skills.api.combat.Callback;
import de.raidcraft.skills.api.combat.RangedCallback;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.exceptions.InvalidChoiceException;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.level.Level;
import de.raidcraft.skills.api.level.Levelable;
import de.raidcraft.skills.api.persistance.Equipment;
import de.raidcraft.skills.api.persistance.HeroData;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.hero.HeroLevel;
import de.raidcraft.skills.tables.THero;
import de.raidcraft.skills.tables.THeroProfession;
import de.raidcraft.skills.tables.THeroSkill;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * @author Silthus
 */
public abstract class AbstractHero extends AbstractCharacterTemplate implements Hero {

    private final int id;
    private final RCPlayer player;
    private boolean debugging = false;
    private int health;
    private int mana;
    private int stamina;
    private Level<Hero> level;
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
        this.health = data.getHealth();
        this.maxLevel = data.getMaxLevel();
        // load the professions first so we have the skills already loaded
        loadProfessions(data.getProfessionNames());
        loadSkills();

        attachLevel(new HeroLevel(this, data.getLevelData()));
        if (professions.size() > 0 && data.getSelectedProfession() != null) {
            this.selectedProfession = professions.get(data.getSelectedProfession().getName());
        }
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
                // only add active skills
                if (skill.isActive()) {
                    skills.put(skill.getName(), skill);
                }
            }
        }
    }

    @Override
    public void changeProfession(Profession profession) {

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
        // go thru all skills and add/remove them from the skill list
        for (Skill skill : profession.getSkills()) {
            skills.put(skill.getName(), skill);
        }
        for (Skill skill : new ArrayList<>(skills.values())) {
            if (!skill.isActive()) {
                skill.save();
                skills.remove(skill.getName());
            } else if (!skill.isUnlocked() && skill.getProfession().getLevel().getLevel() >= skill.getProperties().getRequiredLevel()) {
                skill.unlock();
            }
        }
        save();
    }

    @Override
    public final void runSkill(Skill skill) throws CombatException, InvalidTargetException {

        if (skill instanceof Passive) {
            throw new CombatException(CombatException.Type.PASSIVE);
        }
        // lets check the resources of the skill and if the hero has it
        if (skill.getTotalManaCost() > getMana()) {
            throw new CombatException(CombatException.Type.LOW_MANA);
        }
        if (skill.getTotalStaminaCost() > getStamina()) {
            throw new CombatException(CombatException.Type.LOW_STAMINA);
        }
        if (skill.getTotalHealthCost() > getHealth()) {
            throw new CombatException(CombatException.Type.LOW_HEALTH);
        }
        // lets check if the player has the required reagents
        for (ItemStack itemStack : skill.getProperties().getReagents()) {
            if (!getPlayer().getInventory().contains(itemStack)) {
                throw new CombatException(CombatException.Type.MISSING_REAGENT);
            }
        }

        // TODO: do some fancy checks for the resistence and stuff

        if (skill instanceof TargetedAttack) {
            ((TargetedAttack) skill).run(this, player.getTarget());
        } else if (skill instanceof AreaAttack) {
            ((AreaAttack) skill).run(this, BukkitUtil.toBlock(player.getTargetBlock()).getLocation());
        }
        // keep this last or items will be removed before casting
        getPlayer().getInventory().removeItem(skill.getProperties().getReagents());
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
    }

    @Override
    public int getMaxHealth() {

        return (int) (primaryProfession.getProperties().getBaseHealth()
                        + primaryProfession.getProperties().getBaseHealthModifier() * primaryProfession.getLevel().getLevel());
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
    public void attachLevel(Level<Hero> level) {

        this.level = level;
    }

    @Override
    public void save() {

        saveProfessions();
        saveLevelProgress(level);
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
        if (selectedProfession != null)
            heroTable.setSelectedProfession(Ebean.find(THeroProfession.class, selectedProfession.getId()));
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

        return selectedProfession;
    }

    @Override
    public void setSelectedProfession(Profession profession) {

        this.selectedProfession = profession;
        //TODO: update graphics
    }

    @Override
    public Set<Equipment> getEquipment() {

        return equipment;
    }

    @Override
    public Level<Hero> getLevel() {

        return level;
    }

    @Override
    public int getMaxLevel() {

        return maxLevel;
    }

    @Override
    public void onLevelUp(Level<Hero> level) {

        // override if needed
    }

    @Override
    public void onLevelDown(Level<Hero> level) {

        // override if needed
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
    public void damageEntity(LivingEntity target, int damage) throws CombatException {

        RaidCraft.getComponent(SkillsPlugin.class).getCombatManager().damageEntity(getPlayer(), target, damage);
    }

    @Override
    public void damageEntity(LivingEntity target, int damage, Callback callback) throws CombatException {

        RaidCraft.getComponent(SkillsPlugin.class).getCombatManager().damageEntity(getPlayer(), target, damage, callback);
    }

    @Override
    public void castRangeAttack(RangedCallback callback) {

        RaidCraft.getComponent(SkillsPlugin.class).getCombatManager().castRangeAttack(getPlayer(), callback);
    }

    @Override
    public void kill(LivingEntity attacker) {
        //TODO: implement
    }

    @Override
    public void kill() {
        //TODO: implement
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
