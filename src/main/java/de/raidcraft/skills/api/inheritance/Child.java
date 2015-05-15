package de.raidcraft.skills.api.inheritance;

/**
 * @author Silthus
 */
public interface Child<T extends Parent> {

    boolean hasParent();

    T getParent();

    void setParent(T parent);
}
