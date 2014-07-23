package de.raidcraft.skills.config;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.ambient.AmbientEffect;
import de.raidcraft.api.ambient.AmbientManager;
import de.raidcraft.api.ambient.UnknownAmbientEffect;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.skills.AbilityFactory;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.ability.AbilityInformation;
import de.raidcraft.skills.api.combat.EffectElement;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.persistance.AbilityProperties;
import de.raidcraft.skills.api.skill.AbilityEffectStage;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Silthus
 */
public class AbilityConfig extends ConfigurationBase<SkillsPlugin> implements AbilityProperties<AbilityInformation> {

    private final String name;
    private final AbilityInformation information;

    public AbilityConfig(AbilityFactory factory) {

        super(factory.getPlugin(), new File(
                new File(factory.getPlugin().getDataFolder(), factory.getPlugin().getCommonConfig().skill_config_path),
                factory.getName() + ".yml"));
        this.name = (factory.useAlias() ? factory.getAlias() : factory.getName());
        this.information = factory.getInformation();
    }

    @Override
    public String getFriendlyName() {

        return getOverride("name", getName());
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public String getDescription() {

        return getOverride("description", getInformation().description());
    }

    @Override
    public String[] getUsage() {

        List<String> usage = getStringList("usage");
        return usage.toArray(new String[usage.size()]);
    }

    @Override
    public boolean isEnabled() {

        return getOverrideBool("enabled", true);
    }

    @Override
    public void setEnabled(boolean enabled) {

        set("enabled", enabled);
        save();
    }

    @Override
    public boolean isLevelable() {

        return getOverrideBool("levelable", true);
    }

    @Override
    public boolean canUseInCombat() {

        return getOverrideBool("use-in-combat", true);
    }

    @Override
    public boolean canUseOutOfCombat() {

        return getOverrideBool("use-out-of-combat", true);
    }

    @Override
    public Map<AbilityEffectStage, List<AmbientEffect>> getAmbientEffects() {

        HashMap<AbilityEffectStage, List<AmbientEffect>> effects = new HashMap<>();
        ConfigurationSection root = getOverrideSection("ambient-effects");
        for (AbilityEffectStage stage : AbilityEffectStage.values()) {
            ConfigurationSection section = root.getConfigurationSection(stage.name());
            if (section == null) {
                continue;
            }
            if (!effects.containsKey(stage)) {
                effects.put(stage, new ArrayList<>());
            }
            Set<String> keys = section.getKeys(false);
            for (String key : keys) {
                try {
                    effects.get(stage).add(AmbientManager.getEffect(section.getConfigurationSection(key)));
                } catch (UnknownAmbientEffect e) {
                    RaidCraft.LOGGER.warning(e.getMessage());
                }
            }
        }
        return effects;
    }

    @Override
    public Set<EffectType> getTypes() {

        HashSet<EffectType> types = new HashSet<>();
        for (String str : getStringList("types")) {
            EffectType type = EffectType.fromString(str);
            if (type == null) {
                getPlugin().getLogger().warning("Wrong effect type " + str + " defined in config " + getName());
                continue;
            }
            types.add(type);
        }
        return types;
    }

    @Override
    public Set<EffectElement> getElements() {

        HashSet<EffectElement> elements = new HashSet<>();
        for (String str : getStringList("elements")) {
            EffectElement element = EffectElement.fromString(str);
            if (element == null) {
                getPlugin().getLogger().warning("Wrong effect element " + str + " defined in config " + getName());
                continue;
            }
            elements.add(element);
        }
        return elements;
    }

    @Override
    public ConfigurationSection getData() {

        return getOverrideSection("custom");
    }

    @Override
    public AbilityInformation getInformation() {

        return information;
    }

    @Override
    public ConfigurationSection getDamage() {

        return getOverrideSection("damage");
    }

    @Override
    public ConfigurationSection getCastTime() {

        return getOverrideSection("casttime");
    }

    @Override
    public ConfigurationSection getRange() {

        return getOverrideSection("range");
    }

    @Override
    public ConfigurationSection getCooldown() {

        return getOverrideSection("cooldown");
    }
}
