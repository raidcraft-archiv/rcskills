package de.raidcraft.skills.config;

import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.skills.SkillsPlugin;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public class LevelConfig extends ConfigurationBase<SkillsPlugin> {

    private final Map<String, ConfigurationSection> skillFormulas = new HashMap<>();
    private final Map<String, ConfigurationSection> professionFormulas = new HashMap<>();
    private final Map<String, ConfigurationSection> heroFormulas = new HashMap<>();

    public LevelConfig(SkillsPlugin plugin) {

        super(plugin, "levels.yml");
    }

    public void loadFormulas() {

        loadFormulas(getConfigurationSection("skills"), skillFormulas);
        loadFormulas(getConfigurationSection("professions"), professionFormulas);
        loadFormulas(getConfigurationSection("heroes"), heroFormulas);
    }

    private void loadFormulas(ConfigurationSection section, Map<String, ConfigurationSection> map) {

        if (section == null) return;

        for (String key : section.getKeys(false)) {
            map.put(key, section.getConfigurationSection(key));
        }
    }

    public ConfigurationSection getConfigFor(Type type, String name) {

        switch (type) {

            case SKILLS:
                if (!skillFormulas.containsKey(name)) {
                    return skillFormulas.get("default");
                }
                return skillFormulas.get(name);
            case PROFESSIONS:
                if (!professionFormulas.containsKey(name)) {
                    return professionFormulas.get("default");
                }
                return professionFormulas.get(name);
            case HEROES:
                if (!heroFormulas.containsKey(name)) {
                    return heroFormulas.get("default");
                }
                return heroFormulas.get(name);
            default:
                return null;
        }
    }

    public enum Type {

        SKILLS,
        PROFESSIONS,
        HEROES
    }
}
