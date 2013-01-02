package de.raidcraft.skills.api.requirement;

/**
 * @author Silthus
 */
public abstract class AbstractRequirement<T> implements Requirement<T> {

    private final T type;

    public AbstractRequirement(T type) {

        this.type = type;
    }

    @Override
    public T getType() {

        return type;
    }
}
