package de.raidcraft.skills.api.combat.action;

/**
 * @author Silthus
 */
public abstract class AbstractAction<S> implements Action<S> {

    private final S source;

    protected AbstractAction(final S source) {

        this.source = source;
    }

    @Override
    public S getSource() {

        return source;
    }
}
