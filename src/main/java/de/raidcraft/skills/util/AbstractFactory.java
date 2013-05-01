package de.raidcraft.skills.util;

import de.raidcraft.skills.SkillsPlugin;

/**
 * @author Silthus
 */
public abstract class AbstractFactory<T> {

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

    public abstract T getInformation();
}
