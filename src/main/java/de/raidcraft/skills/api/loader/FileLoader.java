package de.raidcraft.skills.api.loader;

import java.util.logging.Logger;

/**
 * A parent class for skill loaders that load components from the raw filesystem.
 */
public abstract class FileLoader<T> extends AbstractLoader<T> {


    protected FileLoader(Class<T> tClass, Logger logger) {

        super(tClass, logger);
    }

    public String formatPath(String path) {
        if (path.length() < 6) return path;
        return path.substring(0, path.length() - 6).replaceAll("/", ".");
    }
}
