package de.raidcraft.skills.config;

import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.persistance.ProfessionData;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Silthus
 */
public class ProfessionConfig {

    private final SkillsPlugin plugin;

    public ProfessionConfig(SkillsPlugin plugin) {

        this.plugin = plugin;
    }

    public ProfessionData getProfessionData(String id) {

        return new Data(id, YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + "/professions/", id + ".yml")));
    }

    public class Data extends ProfessionData {

        public Data(String id, YamlConfiguration config, String... exclude) {

            super(config.getConfigurationSection("custom"), exclude);
            this.name = id;
            this.friendlyName = config.getString("name");
            this.description = config.getString("description");
            this.skills = loadSkills(getStringList("skills"));
            this.strongParents = loadParents(getStringList("strong-parents"));
            this.weakParents = loadParents(getStringList("weak-parents"));
        }

        private Set<Skill> loadSkills(List<String> names) {

            Set<Skill> skills = new HashSet<>();
            for (String s : names) {
                try {
                    skills.add(plugin.getSkillManager().getSkill(s));
                } catch (UnknownSkillException e) {
                    plugin.getLogger().severe("The skill " + s + " does not exist. Occured when loading: " + name);
                    e.printStackTrace();
                }
            }
            return skills;
        }

        private Set<Profession> loadParents(List<String> names) {

            Set<Profession> professions = new HashSet<>();
            for (String p : names) {
                try {
                    professions.add(plugin.getProfessionManager().getProfession(p));
                } catch (UnknownProfessionException e) {
                    plugin.getLogger().severe("The parent " + p + " of " + name + " does not exist. Please fix it...");
                    e.printStackTrace();
                }
            }
            return professions;
        }

    }
}
