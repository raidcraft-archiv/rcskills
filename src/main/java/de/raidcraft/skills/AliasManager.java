package de.raidcraft.skills;

import de.raidcraft.api.config.ConfigLoader;
import de.raidcraft.api.quests.Quests;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.config.AliasesConfig;
import de.raidcraft.skills.util.StringUtils;
import de.raidcraft.util.CaseInsensitiveMap;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.Map;

/**
 * @author Silthus
 */
public final class AliasManager {

    private final SkillsPlugin plugin;
    private final File configPath;
    private final Map<String, AliasesConfig> aliasConfigs = new CaseInsensitiveMap<>();
    private final Map<String, String> aliasSkillMapping = new CaseInsensitiveMap<>();
    private int loadedAliases;
    private int failedAliases;

    public AliasManager(SkillsPlugin plugin) {

        this.plugin = plugin;
        // create the config path
        this.configPath = new File(plugin.getDataFolder(), plugin.getCommonConfig().alias_config_path);
        this.configPath.mkdirs();
        loadAliases();
    }

    private void loadAliases() {

        loadAliasConfig(configPath);
        // now lets create factories for every alias based on their skill
        for (Map.Entry<String, String> entry : aliasSkillMapping.entrySet()) {
            try {
                plugin.getSkillManager().createAliasFactory(entry.getKey(), entry.getValue(), aliasConfigs.get(entry.getKey()));
                loadedAliases++;
            } catch (UnknownSkillException e) {
                plugin.getLogger().warning(e.getMessage());
                failedAliases++;
            }
        }
        plugin.getLogger().info("Loaded " + loadedAliases + "/" + (failedAliases + loadedAliases) + " alias skills.");
    }

    private void loadAliasConfig(File dir) {

        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                // recursive loading of all sub directories
                loadAliasConfig(file);
            }
            if (file.getName().endsWith(".yml")) {
                String alias = StringUtils.formatName(file.getName().replace(".yml", ""));
                AliasesConfig config = plugin.configure(new AliasesConfig(plugin, file, alias));
                if (config.getString("skill") == null || !plugin.getSkillManager().hasSkill(config.getString("skill"))) {
                    plugin.getLogger().warning(
                            "Der Alias " + alias + " ist falsch konfiguriert! Es gibt keinen Skill: " + config.getString("skill"));
                    failedAliases++;
                } else if (plugin.getSkillManager().hasSkill(alias)) {
                    plugin.getLogger().warning(
                            "Der Alias " + alias + " ist falsch konfiguriert! Es gibt bereits einen Skill mit dem Namen: " + alias);
                    failedAliases++;
                } else {
                    aliasSkillMapping.put(alias, config.getString("skill"));
                    aliasConfigs.put(alias, config);
                }
            }
        }
    }

    public boolean isAlias(String alias) {

        return aliasSkillMapping.containsKey(alias);
    }

    public String getSkillNameOfAlias(String alias) {

        return aliasSkillMapping.get(alias);
    }

    public AliasesConfig getAliasConfig(String alias) {

        return aliasConfigs.get(alias);
    }
}
