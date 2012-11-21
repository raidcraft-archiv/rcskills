package de.raidcraft.skills.config;

import com.avaje.ebean.Ebean;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.LevelData;
import de.raidcraft.skills.api.persistance.ProfessionData;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.tables.THero;
import de.raidcraft.skills.tables.THeroProfession;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * @author Silthus
 */
public class ProfessionConfig extends YamlConfiguration implements ProfessionData, LevelData {

    private final Hero hero;
    private final SkillsPlugin plugin;
    private THeroProfession profession;
    private Set<Skill> skills;

    public ProfessionConfig(SkillsPlugin plugin, Hero hero, String name) throws UnknownProfessionException, UnknownSkillException {

        this.hero = hero;
        this.plugin = plugin;

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

        // then load the hero stats from the database
        profession = Ebean.find(THeroProfession.class).where().eq("name", name).eq("hero_id", hero.getId()).findUnique();
        if (profession == null) {
            // create a new entry
            profession = new THeroProfession();
            profession.setHero(Ebean.find(THero.class, hero.getId()));
            profession.setLevel(1);
            profession.setExp(0);
            profession.setMastered(false);
            profession.setActive(false);
            Ebean.save(profession);
        }

        // now load the skills - when a skill does not exist in the database we will insert it
        Set<String> configSkills = getConfigurationSection("skills").getKeys(false);
        for (String skill : configSkills) {
            this.skills.add(plugin.getSkillManager().loadSkill(hero, skill, this));
        }
    }

    @Override
    public int getId() {

        return profession.getId();
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

        return profession.isMastered();
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

        return profession.getLevel();
    }

    @Override
    public int getExp() {

        return profession.getExp();
    }

    @Override
    public int getMaxLevel() {

        return getInt("max-exp");
    }
}
