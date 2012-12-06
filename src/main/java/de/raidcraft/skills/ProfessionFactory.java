package de.raidcraft.skills;

import com.avaje.ebean.Ebean;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.ProfessionProperties;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.professions.SimpleProfession;
import de.raidcraft.skills.tables.THero;
import de.raidcraft.skills.tables.THeroProfession;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
public final class ProfessionFactory extends YamlConfiguration implements ProfessionProperties {

    private final SkillsPlugin plugin;
    private final File file;
    private final String name;

    protected ProfessionFactory(SkillsPlugin plugin, File file) {

        this.plugin = plugin;
        // we asume the file always exist because the skillmanager passed it to us
        this.file = file;
        this.name = file.getName().toLowerCase().replace("profession", "").replace(".yml", "").trim();
        // first load all common information about this profession
        loadFile();
        plugin.getLogger().info("Profession loaded: " + name);
    }

    protected Profession create(Hero hero) throws UnknownSkillException {

        return new SimpleProfession(hero, this, loadDatabase(hero, name));
    }

    private THeroProfession loadDatabase(Hero hero, String name) {

        // then load the hero stats from the database
        THeroProfession database = Ebean.find(THeroProfession.class).where()
                .eq("name", name)
                .eq("hero_id", hero.getId()).findUnique();
        if (database == null) {
            // create a new entry
            database = new THeroProfession();
            database.setName(getName());
            database.setHero(Ebean.find(THero.class, hero.getId()));
            database.setLevel(1);
            database.setExp(0);
            database.setMastered(false);
            database.setActive(false);
            Ebean.save(database);
        }
        return database;
    }

    private void loadFile() {

        try {
            // load the flat file config
            load(file);
        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().warning(e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Skill> loadSkills(Hero hero, Profession profession) {

        List<Skill> skills = new ArrayList<>();
        // now load the skills - when a skill does not exist in the database we will insert it
        for (String skill : getConfigurationSection("skills").getKeys(false)) {
            try {
                skills.add(plugin.getSkillManager().getSkill(hero, this, profession, skill));
            } catch (UnknownSkillException e) {
                plugin.getLogger().warning(e.getMessage());
                e.printStackTrace();
            }
        }
        return skills;
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public String getTag() {

        return getString("tag");
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
    public int getMaxLevel() {

        return getInt("max-level", 60);
    }

    @Override
    public int getBaseHealth() {

        return getInt("base-health", 20);
    }

    @Override
    public double getBaseHealthModifier() {

        return getDouble("base-health-level-modifier", 0.0);
    }

    @Override
    public boolean isPrimary() {

        return getBoolean("primary", false);
    }
}
