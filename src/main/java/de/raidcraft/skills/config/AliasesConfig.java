package de.raidcraft.skills.config;

import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.util.StringUtil;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
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

        super(plugin, new File(plugin.getDataFolder(), CONFIG_NAME));
        loadEffects();
        loadSkills();
    }

    private void loadEffects() {

        ConfigurationSection section = getConfigurationSection("effects");
        if (section == null) {
            section = createSection("effects");
        }
        for (String key : section.getKeys(false)) {
            ConfigurationSection override = section.getConfigurationSection(key);
            String effect = override.getString("effect");
            if (effect == null || effect.equals("") || !getPlugin().getEffectManager().hasEffect(effect)) {
                getPlugin().getLogger().warning("effect " + effect + " in alias " + key + " does not exist!");
            } else {
                effects.put(key, override);
            }
        }
    }

    public void loadSkills() {

        ConfigurationSection section = getConfigurationSection("skills");
        if (section == null) {
            section = createSection("skills");
        }
        for (String key : section.getKeys(false)) {
            ConfigurationSection override = section.getConfigurationSection(key);
            String skill = override.getString("skill");
            if (skill == null || skill.equals("") || !getPlugin().getSkillManager().hasSkill(skill)) {
                getPlugin().getLogger().warning("skill " + skill + " in alias " + key + " does not exist!");
            } else {
                skills.put(key, override);
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
}
