package de.raidcraft.skills.config;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.util.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public class CustomConfig extends ConfigurationBase<SkillsPlugin> {

    private static final SkillsPlugin PLUGIN = RaidCraft.getComponent(SkillsPlugin.class);
    private static final Map<String, CustomConfig> customConfigs = new HashMap<>();

    public CustomConfig(SkillsPlugin plugin, File file) {

        super(plugin, file);
    }

    public static CustomConfig getConfig(String name) {

        name = StringUtils.formatName(name);
        if (!name.endsWith(".yml")) {
            name = name + ".yml";
        }
        CustomConfig config;
        if (!customConfigs.containsKey(name)) {
            File dir = new File(PLUGIN.getDataFolder(), "custom-configs");
            dir.mkdirs();
            config = PLUGIN.configure(new CustomConfig(PLUGIN, new File(dir, name)), false);
            customConfigs.put(name, config);
        } else {
            config = customConfigs.get(name);
        }
        return config;
    }
}
