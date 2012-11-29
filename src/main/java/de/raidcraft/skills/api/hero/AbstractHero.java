package de.raidcraft.skills.api.hero;

import com.avaje.ebean.Ebean;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.util.StringUtil;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.bukkit.BukkitPlayer;
import de.raidcraft.skills.ProfessionManager;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.level.Level;
import de.raidcraft.skills.api.level.Levelable;
import de.raidcraft.skills.api.persistance.HeroData;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.hero.HeroLevel;
import de.raidcraft.skills.tables.THero;
import de.raidcraft.skills.tables.THeroSkill;
import org.bukkit.entity.LivingEntity;

import java.util.*;

/**
 * @author Silthus
 */
public abstract class AbstractHero extends BukkitPlayer implements Hero {

    private final int id;
    private boolean inCombat = false;
    private int health;
    private Level<Hero> level;
    private int maxLevel;
    private final Map<String, Skill> skills = new HashMap<>();
    private final Map<String, Profession> professions = new HashMap<>();
    // primary and secondary professions are the ones defining items and stuff
    private Profession primaryProfession;
    private Profession secundaryProfession;
    // this just tells the client what to display in the experience bar and so on
    private Profession selectedProfession;

    protected AbstractHero(HeroData data) {

        super(data.getName());

        this.id = data.getId();
        this.health = data.getHealth();
        this.maxLevel = data.getMaxLevel();
        // load the professions first so we have the skills already loaded
        loadProfessions(data.getProfessionNames());
        loadSkills();

        attachLevel(new HeroLevel(this, data.getLevelData()));
        if (professions.size() > 0 && data.getSelectedProfession() != null) {
            this.selectedProfession = professions.get(data.getSelectedProfession().getName());
        }
    }

    private void loadProfessions(List<String> professionNames) {

        ProfessionManager manager = RaidCraft.getComponent(SkillsPlugin.class).getProfessionManager();
        for (String professionName : professionNames) {
            try {
                Profession profession = manager.getProfession(this, professionName);
                professions.put(profession.getProperties().getName().toLowerCase(), profession);
            } catch (UnknownSkillException e) {
                RaidCraft.LOGGER.warning(e.getMessage());
                e.printStackTrace();
            } catch (UnknownProfessionException e) {
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
                skills.put(skill.getName().toLowerCase(), skill);
            }
        }
    }

    @Override
    public int getId() {

        return id;
    }

    @Override
    public boolean isInCombat() {

        return inCombat;
    }

    @Override
    public void setInCombat(boolean inCombat) {

        this.inCombat = inCombat;
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
    public void attachLevel(Level<Hero> level) {

        this.level = level;
    }

    @Override
    public final void save() {

        saveLevelProgress(level);
        saveSkills();
    }

    @Override
    public void saveLevelProgress(Level<Hero> level) {

        THero heroTable = Ebean.find(THero.class, getId());
        heroTable.setExp(getLevel().getExp());
        heroTable.setLevel(getLevel().getLevel());
        Ebean.save(heroTable);
    }

    @Override
    public final void saveSkills() {

        for (Skill skill : getSkills()) {
            if (skill instanceof Levelable) {
                ((Levelable) skill).getLevel().saveLevelProgress();
            }
        }
    }

    @Override
    public boolean hasSkill(String id) {

        id = id.toLowerCase();
        if (isOnline()) {
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
    public Collection<Skill> getSkills() {

        return skills.values();
    }

    @Override
    public Collection<Skill> getUnlockedSkills() {

        List<Skill> skills = new ArrayList<>();
        for (Skill skill : this.skills.values()) {
            if (skill.isUnlocked()) {
                skills.add(skill);
            }
        }
        return skills;
    }

    @Override
    public Collection<Profession> getProfessions() {

        return professions.values();
    }

    @Override
    public Profession getSelectedProfession() {

        return selectedProfession;
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
    public void increaseLevel(Level<Hero> level) {

        // override if needed
    }

    @Override
    public void decreaseLevel(Level<Hero> level) {

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

        RaidCraft.getComponent(SkillsPlugin.class).getCombatManager().damageEntity(getBukkitPlayer(), target, damage);
    }

    @Override
    public Skill getSkillFromArg(String input) throws CommandException {

        List<String> foundSkills = new ArrayList<>();
        input = input.toLowerCase().trim();
        for (Skill skill : skills.values()) {
            if (skill.getName().toLowerCase().contains(input)
                    || skill.getFriendlyName().toLowerCase().contains(input)) {
                foundSkills.add(skill.getName());
            }
        }

        if (foundSkills.size() > 1) {
            throw new CommandException(
                    "Es gibt mehrere Skills mit dem Namen: " + input + " - " + StringUtil.joinString(foundSkills, ", ", 0));
        }

        return skills.get(foundSkills.get(0));
    }
}
