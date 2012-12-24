package de.raidcraft.skills.config;

import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.util.StringUtil;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public final class AliasesConfig extends ConfigurationBase<SkillsPlugin> {

    private static final String CONFIG_NAME = "aliases.yml";
    private final Map<String, ConfigurationSection> effects = new HashMap<>();
    private final Map<String, ConfigurationSection> skills = new HashMap<>();

    public AliasesConfig(SkillsPlugin plugin) {

        super(plugin, CONFIG_NAME);
    }

    @Override
    public void load() {

        super.load();
        loadEffects();
        loadSkills();
    }

    private void loadEffects() {

        ConfigurationSection section = getSafeConfigSection("effects");
        for (String key : section.getKeys(false)) {
            ConfigurationSection override = section.getConfigurationSection(key);
            String effect = StringUtil.formatName(override.getString("effect"));
            if (effect == null || effect.equals("")) {
                getPlugin().getLogger().warning("effect " + effect + " in alias " + key + " does not exist!");
            } else {
                effects.put(StringUtil.formatName(key), override);
            }
        }
    }

    public void loadSkills() {

        ConfigurationSection section = getSafeConfigSection("skills");
        for (String key : section.getKeys(false)) {
            ConfigurationSection override = section.getConfigurationSection(key);
            String skill = StringUtil.formatName(override.getString("skill"));
            if (skill == null || skill.equals("")) {
                getPlugin().getLogger().warning("skill " + skill + " in alias " + key + " does not exist!");
            } else {
                skills.put(StringUtil.formatName(key), override);
            }
        }
    }

    public boolean hasEffect(String name, String effectName) {

        return effects.containsKey(name) && effects.get(name).getString("effect").equals(effectName);
    }

    public boolean hasSkill(String name, String skillName) {

        return skills.containsKey(name) && skills.get(name).getString("skill").equals(skillName);
    }

    public ConfigurationSection getEffectConfig(String name) {

        return effects.get(name);
    }

    public ConfigurationSection getSkillConfig(String name) {

        return skills.get(name);
    }

    public boolean hasSkill(String name) {

        return skills.containsKey(name);
    }

    public boolean hasEffect(String effect) {

        return effects.containsKey(effect);
    }

    public String getSkillName(String alias) {

        return StringUtil.formatName(skills.get(alias).getString("skill"));
    }

    public String getEffectName(String alias) {

        return StringUtil.formatName(effects.get(alias).getString("effect"));
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
