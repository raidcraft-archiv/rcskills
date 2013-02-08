package de.raidcraft.skills.api.inheritance;

import java.util.List;

/**
 * @author Silthus
 */
public interface Parent<T extends Child> {

    public boolean hasChildren();

    public List<T> getChildren();
}
