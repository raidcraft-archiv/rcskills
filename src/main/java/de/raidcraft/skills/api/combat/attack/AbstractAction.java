package de.raidcraft.skills.api.combat.attack;

/**
 * @author Silthus
 */
public abstract class AbstractAction<T> implements Action<T> {

    private final T source;

    protected AbstractAction(T source) {

        this.source = source;
    }

    @Override
    public T getSource() {

        return source;
    }
}
