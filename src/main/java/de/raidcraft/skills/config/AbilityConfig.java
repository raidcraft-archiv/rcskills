package de.raidcraft.skills.config;

import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.skills.AbilityFactory;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.ability.AbilityInformation;
import de.raidcraft.skills.api.persistance.AbilityProperties;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.List;

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
    public String getName() {

        return name;
    }

    @Override
    public AbilityInformation getInformation() {

        return information;
    }

    @Override
    public String getFriendlyName() {

        return getOverride("name", getName());
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

        return getBoolean("enabled", true);
    }

    @Override
    public void setEnabled(boolean enabled) {

        set("enabled", enabled);
        save();
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
    public ConfigurationSection getData() {

        return getOverrideSection("custom");
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
    public ConfigurationSection getCooldown() {

        return getOverrideSection("cooldown");
    }

    @Override
    public ConfigurationSection getRange() {

        return getOverrideSection("range");
    }
}
