package de.raidcraft.skills.config;

import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.skills.SkillsPlugin;

import java.io.File;

/**
 * @author Silthus
 */
public final class AliasesConfig extends ConfigurationBase<SkillsPlugin> {

    private final String aliasName;

    public AliasesConfig(SkillsPlugin plugin, File file, String aliasName) {

        super(plugin, file);
        this.aliasName = aliasName;
    }

    @Override
    public String getName() {

        return aliasName;
    }
}
