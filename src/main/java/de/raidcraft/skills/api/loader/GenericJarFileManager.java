package de.raidcraft.skills.api.loader;

import de.raidcraft.skills.SkillsPlugin;

import java.io.File;

/**
 * @author Silthus
 */
public abstract class GenericJarFileManager<T> extends JarFileLoader<T> {

    protected final SkillsPlugin plugin;
    protected final File configDir;

    protected GenericJarFileManager(Class<T> tClass, SkillsPlugin plugin) {

        super(tClass, plugin.getLogger(), new File(plugin.getDataFolder(), "/" + tClass.getSimpleName().toLowerCase() + "s/"));
        this.plugin = plugin;

        // lets go thru all the skill configs and remove the .disabled
        this.configDir = new File(plugin.getDataFolder(), "/" + tClass.getSimpleName().toLowerCase() + "-configs/");
        configDir.mkdirs();

        loadFactories();
    }

    protected abstract void loadFactories();
}
