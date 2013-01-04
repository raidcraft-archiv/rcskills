package de.raidcraft.skills.api.loader;

/**
 * A parent class for skill loaders that load components from the raw filesystem.
 */
public abstract class FileLoader<T> extends AbstractLoader<T> {


    protected FileLoader(Class<T> tClass) {

        super(tClass);
    }

    public String formatPath(String path) {

        if (path.length() < 6) return path;
        return path.substring(0, path.length() - 6).replaceAll("/", ".");
    }
}
