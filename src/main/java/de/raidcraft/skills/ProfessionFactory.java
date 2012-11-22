package de.raidcraft.skills;

import com.avaje.ebean.Ebean;
import de.raidcraft.skills.api.Factory;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.LevelData;
import de.raidcraft.skills.api.persistance.ProfessionData;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.professions.SimpleProfession;
import de.raidcraft.skills.tables.THero;
import de.raidcraft.skills.tables.THeroProfession;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Silthus
 */
public final class ProfessionFactory extends YamlConfiguration implements ProfessionData, LevelData, Factory<Profession> {

    private Profession profession = null;
    private final SkillsPlugin plugin;
    private final Hero hero;
    private THeroProfession database;
    private Set<Skill> skills = new LinkedHashSet<>();

    protected ProfessionFactory(SkillsPlugin plugin, Hero hero, THeroProfession database) throws UnknownProfessionException, UnknownSkillException {

        this.plugin = plugin;
        this.hero = hero;
        loadInformation(database.getName());
        // here we skill database loading and save the already existing entry
        this.database = database;
        loadSkills();
    }

    protected ProfessionFactory(SkillsPlugin plugin, Hero hero, String name) throws UnknownProfessionException, UnknownSkillException {

        this.plugin = plugin;
        this.hero = hero;
        loadInformation(name);
        loadDatabase(name);
        loadSkills();
    }

    public Profession create() {

        if (this.profession == null) {
            this.profession = new SimpleProfession(hero, this);
        }
        return this.profession;
    }

    private void loadDatabase(String name) {

        // then load the hero stats from the database
        database = Ebean.find(THeroProfession.class).where().eq("name", name).eq("hero_id", hero.getId()).findUnique();
        if (database == null) {
            // create a new entry
            database = new THeroProfession();
            database.setHero(Ebean.find(THero.class, hero.getId()));
            database.setLevel(1);
            database.setExp(0);
            database.setMastered(false);
            database.setActive(false);
            Ebean.save(database);
        }
    }

    private void loadInformation(String name) throws UnknownProfessionException {

        try {
            File file = new File(plugin.getDataFolder() + "/professions/", name + ".yml");
            if (!file.exists()) {
                throw new UnknownProfessionException("There is no profession with the name: " + name);
            }
            // first load the flatfile config
            load(file);
        } catch (IOException e) {
            e.printStackTrace();
            throw new UnknownProfessionException(e.getMessage());
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
            throw new UnknownProfessionException(e.getMessage());
        }


    }

    private void loadSkills() throws UnknownSkillException {

        // now load the skills - when a skill does not exist in the database we will insert it
        Set<String> configSkills = getConfigurationSection("skills").getKeys(false);
        for (String skill : configSkills) {
            this.skills.add(plugin.getSkillManager().loadSkill(hero, skill, this));
        }
    }

    @Override
    public int getId() {

        return database.getId();
    }

    @Override
    public String getFriendlyName() {

        return getString("name");
    }

    @Override
    public String getDescription() {

        return getString("description");
    }

    @Override
    public boolean isActive() {

        return getBoolean("active");
    }

    @Override
    public boolean isMastered() {

        return database.isMastered();
    }

    @Override
    public LevelData getLevelData() {

        return this;
    }

    @Override
    public Set<Skill> getSkills() {

        return skills;
    }

    @Override
    public int getLevel() {

        return database.getLevel();
    }

    @Override
    public int getExp() {

        return database.getExp();
    }

    @Override
    public int getMaxLevel() {

        return getInt("max-exp");
    }
}
