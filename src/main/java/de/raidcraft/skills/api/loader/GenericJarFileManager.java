package de.raidcraft.skills.api.loader;

import java.io.File;

/**
 * @author Silthus
 */
public abstract class GenericJarFileManager<T> extends JarFileLoader<T> {

    protected GenericJarFileManager(Class<T> tClass, File jarDir) {

        super(tClass, jarDir);
    }

    public abstract void loadFactories();
}
