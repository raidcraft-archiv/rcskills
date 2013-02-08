package de.raidcraft.skills.config;

import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.skills.SkillsPlugin;

/**
 * @author Silthus
 */
public class PathConfig extends ConfigurationBase<SkillsPlugin> {

    public PathConfig(SkillsPlugin plugin) {

        super(plugin, "paths.yml");
    }
}
