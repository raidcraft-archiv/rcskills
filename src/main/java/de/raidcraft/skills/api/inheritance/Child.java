package de.raidcraft.skills.api.inheritance;

/**
 * @author Silthus
 */
public interface Child<T extends Parent> {

    public boolean hasParent();

    public T getParent();

    void setParent(T parent);
}
