package de.raidcraft.skills.config;

import de.raidcraft.api.BasePlugin;
import de.raidcraft.skills.api.persistance.ProfessionData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/**
 * @author Silthus
 */
public class ProfessionConfig {

    private final BasePlugin plugin;

    public ProfessionConfig(BasePlugin plugin) {

        this.plugin = plugin;
    }

    public ProfessionData getProfessionData(String id) {

        return new Data(YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + "/professions/", id + ".yml")));
    }


    public static class Data extends ProfessionData {

        public Data(ConfigurationSection config, String... exclude) {

            super(config.getConfigurationSection("custom"), exclude);
        }
    }
}
