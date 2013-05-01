package de.raidcraft.skills.util;

import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.skill.SkillInformation;

/**
 * @author Silthus
 */
public abstract class AbstractFactory {

    private final SkillsPlugin plugin;
    private final String name;

    protected AbstractFactory(SkillsPlugin plugin, String name) {

        this.plugin = plugin;
        this.name = name;
    }

    public SkillsPlugin getPlugin() {

        return plugin;
    }

    public String getName() {

        return name;
    }

    public abstract boolean useAlias();

    public abstract String getAlias();

    public abstract SkillInformation getInformation();
}
