package de.raidcraft.skills.api.inheritance;

import de.raidcraft.skills.api.path.Path;

import java.util.List;

/**
 * @author Silthus
 */
public interface Parent<T extends Child> {

    public Path getPath();

    public boolean hasChildren();

    public List<T> getChildren();
}
