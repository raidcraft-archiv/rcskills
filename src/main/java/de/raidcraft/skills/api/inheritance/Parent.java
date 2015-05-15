package de.raidcraft.skills.api.inheritance;

import de.raidcraft.skills.api.path.Path;

import java.util.List;

/**
 * @author Silthus
 */
public interface Parent<T extends Child> {

    Path getPath();

    boolean hasChildren();

    List<T> getChildren();
}
