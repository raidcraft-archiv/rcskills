package de.raidcraft.skills.loader;

import java.util.logging.Logger;

/**
 * A parent class for skill loaders that load components from the raw filesystem.
 */
public abstract class FileSkillLoader extends AbstractSkillLoader {

    protected FileSkillLoader(Logger logger) {

        super(logger);
    }
    
    public String formatPath(String path) {
        if (path.length() < 6) return path;
        return path.substring(0, path.length() - 6).replaceAll("/", ".");
    }
}
