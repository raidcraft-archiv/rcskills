package de.raidcraft.skills.api.combat.action;

/**
 * @author Silthus
 */
public abstract class AbstractAction<T> implements Action<T> {

    private final T source;

    protected AbstractAction(final T source) {

        this.source = source;
    }

    @Override
    public T getSource() {

        return source;
    }
}
