package de.raidcraft.skills.api.combat.effect;

import de.raidcraft.api.config.DataMap;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.EffectData;

/**
 * @author Silthus
 */
public abstract class AbstractEffect<S, T> implements Effect<S, T> {

    private final EffectInformation info;
    private final String name;
    private final S source;
    private final T target;
    private double priority;

    public AbstractEffect(S source, T target, EffectData data) {

        this.info = data.getInformation();
        this.name = convertName(info.name());
        this.priority = (data.getEffectPriority() == 0.0 ? info.priority() : data.getEffectPriority());
        this.source = source;
        this.target = target;
        load(data.getDataMap());
    }

    @Override
    public void load(DataMap data) {
        // override if needed
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public String getDescription() {

        return info.description();
    }

    @Override
    public Type[] getTypes() {

        return info.types();
    }

    @Override
    public boolean isOfType(Type type) {

        for (Type t : info.types()) {
            if (type == t) {
                return true;
            }
        }
        return false;
    }

    @Override
    public double getPriority() {

        return priority;
    }

    @Override
    public void setPriority(double priority) {

        this.priority = priority;
    }

    @Override
    public S getSource() {

        return source;
    }

    @Override
    public T getTarget() {

        return target;
    }

    @Override
    public void renew() throws CombatException {

        apply(getTarget());
        if (getSource() instanceof Hero && getTarget() instanceof CharacterTemplate) {
            ((Hero) getSource()).debug("You->" + ((CharacterTemplate) getTarget()).getName() + ": renewed effect - " + getName());
        }
        if (getTarget() instanceof Hero && getSource() instanceof CharacterTemplate) {
            ((Hero) getTarget()).debug(((CharacterTemplate) getSource()).getName() + "->You: renewed effect - " + getName());
        }
    }

    @Override
    public void apply() throws CombatException {

        // TODO: check restistance and that fancy stuff
        apply(getTarget());
        if (getSource() instanceof Hero && getTarget() instanceof CharacterTemplate) {
            ((Hero) getSource()).debug("You->" + ((CharacterTemplate) getTarget()).getName() + ": applied effect - " + getName());
        }
        if (getTarget() instanceof Hero && getSource() instanceof CharacterTemplate) {
            ((Hero) getTarget()).debug(((CharacterTemplate) getSource()).getName() + "->You: applied effect - " + getName());
        }
    }

    @Override
    public void remove() throws CombatException {

        // TODO: check restistance and that fancy stuff
        remove(getTarget());
        if (getSource() instanceof Hero && getTarget() instanceof CharacterTemplate) {
            ((Hero) getSource()).debug("You->" + ((CharacterTemplate) getTarget()).getName() + ": removed effect - " + getName());
        }
        if (getTarget() instanceof Hero && getSource() instanceof CharacterTemplate) {
            ((Hero) getTarget()).debug(((CharacterTemplate) getSource()).getName() + "->You: removed effect - " + getName());
        }
    }

    protected abstract void apply(T target) throws CombatException;

    protected abstract void remove(T target) throws CombatException;

    private String convertName(String name) {

        return name.toLowerCase().replace(" ", "-").trim();
    }

    @Override
    public boolean equals(Object obj) {

        return obj instanceof Effect
                && ((Effect) obj).getName().equalsIgnoreCase(getName());
    }
}
