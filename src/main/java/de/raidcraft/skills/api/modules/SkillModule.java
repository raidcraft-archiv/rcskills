package de.raidcraft.skills.api.modules;

import de.raidcraft.skills.SkillsPlugin;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public abstract class SkillModule {

    private final SkillsPlugin plugin;

    /**
     * Called by the {@link de.raidcraft.skills.SkillsPlugin} to load the module.
     * The module should fetch its configuration and initialize all data, but
     * should not enable itself.
     * Load is called before {@link #enable()}.
     */
    public abstract void load();

    /**
     * Called when the {@link de.raidcraft.skills.SkillsPlugin} is reloaded.
     * The module should clear all caches and reload its config.
     */
    public abstract void reload();

    /**
     * Called to enable the module.
     * Changes to the Skill Engine can now be applied.
     */
    public abstract void enable();

    /**
     * Called to disable the module.
     * The module should clean itself up and remove all changes from the Skill Engine.
     */
    public abstract void disable();

    /**
     * Override this method and return a list of database classes.
     * The {@link SkillsPlugin} will take care of registering the tables.
     */
    public List<Class<?>> getDatabaseClasses() {
        return new ArrayList<>();
    }
}
