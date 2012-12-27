package de.raidcraft.skills.config;

import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.util.StringUtils;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public final class AliasesConfig extends ConfigurationBase<SkillsPlugin> {

    private static final String CONFIG_NAME = "aliases.yml";
    private final Map<String, ConfigurationSection> skills = new HashMap<>();

    public AliasesConfig(SkillsPlugin plugin) {

        super(plugin, CONFIG_NAME);
    }

    @Override
    public void load() {

        super.load();
        loadSkills();
    }

    public void loadSkills() {

        ConfigurationSection section = getSafeConfigSection("skills");
        for (String key : section.getKeys(false)) {
            ConfigurationSection override = section.getConfigurationSection(key);
            String skill = override.getString("skill");
            if (skill == null || skill.equals("")) {
                getPlugin().getLogger().warning("skill " + skill + " in alias " + key + " does not exist!");
            } else {
                skills.put(StringUtils.formatName(key), override);
            }
        }
    }

    public boolean hasSkill(String name, String skillName) {

        return skills.containsKey(name) && skills.get(name).getString("skill").equals(skillName);
    }

    public ConfigurationSection getSkillConfig(String name) {

        return skills.get(name);
    }

    public boolean hasSkill(String name) {

        return skills.containsKey(name);
    }

    public String getSkillName(String alias) {

        return StringUtils.formatName(skills.get(alias).getString("skill"));
    }

    public boolean hasSkillAliasFor(String skillName) {

        return getSkillAliasFor(skillName) != null;
    }

    public String getSkillAliasFor(String skillName) {

        for (Map.Entry<String, ConfigurationSection> entry : skills.entrySet()) {
            if (entry.getValue().getString("skill").equalsIgnoreCase(skillName)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
