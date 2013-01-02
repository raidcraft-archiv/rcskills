package de.raidcraft.skills;

import de.raidcraft.skills.config.AliasesConfig;
import de.raidcraft.skills.util.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public final class AliasManager {

    private final SkillsPlugin plugin;
    private final File configPath;
    private final Map<String, AliasesConfig> aliasConfigs = new HashMap<>();
    private final Map<String, String> aliasSkillMapping = new HashMap<>();

    public AliasManager(SkillsPlugin plugin) {

        this.plugin = plugin;
        // create the config path
        this.configPath = new File(plugin.getDataFolder(), plugin.getCommonConfig().alias_config_path);
        this.configPath.mkdirs();
        loadAliases();
    }

    private void loadAliases() {

        for (File file : configPath.listFiles()) {
            if (file.getName().endsWith(".yml")) {
                String alias = StringUtils.formatName(file.getName().replace(".yml", ""));
                AliasesConfig config = plugin.configure(new AliasesConfig(plugin, file, alias));
                if (config.getString("skill") == null || !plugin.getSkillManager().hasSkill(config.getString("skill"))) {
                    plugin.getLogger().warning(
                            "Der Alias " + alias + " ist falsch konfiguriert! Es gibt keinen Skill: " + config.getString("skill"));
                } else if (plugin.getSkillManager().hasSkill(alias)) {
                    plugin.getLogger().warning(
                            "Der Alias " + alias + " ist falsch konfiguriert! Es gibt bereits einen Skill mit dem Namen: " + alias);
                } else {
                    aliasSkillMapping.put(alias, config.getString("skill"));
                    aliasConfigs.put(alias, config);
                }
            }
        }
        // now lets create factories for every alias based on their skill
        for (Map.Entry<String, String> entry : aliasSkillMapping.entrySet()) {
            plugin.getSkillManager().createAliasFactory(entry.getKey(), entry.getValue(), aliasConfigs.get(entry.getKey()));
        }
    }

    public void reload() {

        aliasConfigs.clear();
        aliasSkillMapping.clear();
        loadAliases();
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
